package com.unocode.slowme.room.application;

import com.unocode.slowme.common.error.code.ErrorCode;
import com.unocode.slowme.common.error.exception.SlowmeException;
import com.unocode.slowme.room.application.dto.CreateRoomRequest;
import com.unocode.slowme.room.application.dto.JoinLeaveRoomRequest;
import com.unocode.slowme.room.application.dto.RoomSummaryResponse;
import com.unocode.slowme.room.application.port.out.RoomRealtimeEventPublisher;
import com.unocode.slowme.room.application.realtime.RoomRealtimeEvent;
import com.unocode.slowme.room.domain.ParticipantStatus;
import com.unocode.slowme.room.domain.RoomEventType;
import com.unocode.slowme.room.domain.RoomStatus;
import com.unocode.slowme.room.infra.ParticipantEntity;
import com.unocode.slowme.room.infra.ParticipantJpaRepository;
import com.unocode.slowme.room.infra.RoomEntity;
import com.unocode.slowme.room.infra.RoomEventEntity;
import com.unocode.slowme.room.infra.RoomEventJpaRepository;
import com.unocode.slowme.room.infra.RoomJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RoomService {
	private final RoomJpaRepository roomRepo;
	private final ParticipantJpaRepository participantRepo;
	private final RoomEventJpaRepository eventRepo;
	private final EntityManager entityManager;
	private final RoomRealtimeEventPublisher realtimePublisher;

	@Transactional
	public RoomSummaryResponse createRoom(CreateRoomRequest req) {
		RoomEntity room = roomRepo.save(new RoomEntity(req.type(), req.hostId(), req.maxParticipants()));

		// Host automatically joins
		participantRepo.save(new ParticipantEntity(req.hostId(), room.getId(), ParticipantStatus.JOINED));
		// seat reservation (atomic update) - should always succeed right after create
		roomRepo.tryIncrementParticipants(room.getId());
		eventRepo.save(new RoomEventEntity(room.getId(), req.hostId(), RoomEventType.ROOM_CREATED));

		// 명시적 reload: bulk update가 영속성 컨텍스트를 우회하므로 refresh로 안전하게 동기화
		entityManager.refresh(room);
		RoomSummaryResponse res = toSummary(room);
		realtimePublisher.publish(new RoomRealtimeEvent(RoomEventType.ROOM_CREATED, res));
		return res;
	}

	@Transactional(readOnly = true)
	public List<RoomSummaryResponse> listOpenRooms() {
		return roomRepo.findAllByStatus(RoomStatus.OPEN).stream()
				.map(RoomService::toSummary)
				.toList();
	}

	@Transactional
	public RoomSummaryResponse join(Long roomId, JoinLeaveRoomRequest req) {
		// 1) participant 먼저 JOINED로 만들고 (idempotent), 이후 좌석 예약이 실패하면 TX 롤백으로 함께 되돌린다.
		if (participantRepo.existsByRoomIdAndUserIdAndStatus(roomId, req.userId(), ParticipantStatus.JOINED)) {
			throw new SlowmeException(ErrorCode.CONFLICT, "이미 입장한 유저입니다.");
		}

		int updated = participantRepo.markJoinedIfLeft(roomId, req.userId());
		if (updated == 0) {
			try {
				participantRepo.save(new ParticipantEntity(req.userId(), roomId, ParticipantStatus.JOINED));
			} catch (DataIntegrityViolationException e) {
				// unique(roomId,userId) hit: someone else created it. Re-check joined state.
				if (participantRepo.existsByRoomIdAndUserIdAndStatus(roomId, req.userId(), ParticipantStatus.JOINED)) {
					throw new SlowmeException(ErrorCode.CONFLICT, "이미 입장한 유저입니다.", e);
				}
				// unexpected
				throw e;
			}
		}

		// 2) Reserve seat atomically (OPEN + capacity)
		int reserved = roomRepo.tryIncrementParticipants(roomId);
		if (reserved == 0) {
			RoomEntity room = roomRepo.findById(roomId)
					.orElseThrow(() -> new SlowmeException(ErrorCode.NOT_FOUND, "방을 찾을 수 없습니다."));

			if (room.getStatus() != RoomStatus.OPEN) {
				throw new SlowmeException(ErrorCode.CONFLICT, "이미 종료된 방입니다.");
			}
			throw new SlowmeException(ErrorCode.CONFLICT, "방 인원이 가득 찼습니다.");
		}

		RoomEntity room = roomRepo.findById(roomId)
				.orElseThrow(() -> new SlowmeException(ErrorCode.NOT_FOUND, "방을 찾을 수 없습니다."));
		// 이벤트 저장은 항상 마지막에 (락/상태 업데이트 이후)
		eventRepo.save(new RoomEventEntity(roomId, req.userId(), RoomEventType.USER_JOINED));
		RoomSummaryResponse res = toSummary(room);
		realtimePublisher.publish(new RoomRealtimeEvent(RoomEventType.USER_JOINED, res));
		return res;
	}

	@Transactional
	public RoomSummaryResponse leave(Long roomId, JoinLeaveRoomRequest req) {
		// participant -> room 순서로 항상 업데이트해서(Join/Leave 동일) 데드락 가능성을 낮춘다.
		int left = participantRepo.markLeftIfJoined(roomId, req.userId());
		if (left == 0) {
			throw new SlowmeException(ErrorCode.CONFLICT, "입장 상태가 아닙니다.");
		}

		int dec = roomRepo.decrementParticipants(roomId);
		if (dec == 0) {
			// 논리적으로는 발생하면 안 되는 상태(인원수 불일치)
			throw new SlowmeException(ErrorCode.INTERNAL_ERROR, "현재 인원 수 감소에 실패했습니다.");
		}

		RoomEntity after = roomRepo.findById(roomId)
				.orElseThrow(() -> new SlowmeException(ErrorCode.NOT_FOUND, "방을 찾을 수 없습니다."));
		int countAfterLeave = after.getCurrentParticipants();

		boolean hostLeft = req.userId().equals(after.getHostId());
		boolean nobodyLeft = countAfterLeave == 0;

		// close/update는 모두 끝낸 뒤, 이벤트는 마지막에 한 번에 저장한다.
		int closed = 0;
		if (after.getStatus() == RoomStatus.OPEN && (hostLeft || nobodyLeft)) {
			// closeIfOpen이 실제로 상태를 바꿨을 때(1 row)만 ROOM_CLOSED 이벤트를 남긴다.
			closed = roomRepo.closeIfOpen(roomId);
			if (closed == 1) {
				// bulk update는 PC를 갱신하지 않으므로, 명시적으로 현재 엔티티만 refresh
				entityManager.refresh(after);
			}
		}

		// 이벤트는 항상 마지막에 (순서: USER_LEFT → ROOM_CLOSED)
		eventRepo.save(new RoomEventEntity(roomId, req.userId(), RoomEventType.USER_LEFT));
		if (closed == 1) {
			eventRepo.save(new RoomEventEntity(roomId, req.userId(), RoomEventType.ROOM_CLOSED));
		}

		RoomSummaryResponse res = toSummary(after);
		realtimePublisher.publish(new RoomRealtimeEvent(RoomEventType.USER_LEFT, res));
		if (closed == 1) {
			realtimePublisher.publish(new RoomRealtimeEvent(RoomEventType.ROOM_CLOSED, res));
		}
		return res;
	}

	private static RoomSummaryResponse toSummary(RoomEntity room) {
		return new RoomSummaryResponse(
				room.getId(),
				room.getType(),
				room.getStatus(),
				room.getHostId(),
				room.getMaxParticipants(),
				room.getCurrentParticipants()
		);
	}
}


