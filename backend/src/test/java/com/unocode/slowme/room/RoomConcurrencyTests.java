package com.unocode.slowme.room;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.unocode.slowme.common.error.exception.SlowmeException;
import com.unocode.slowme.room.application.RoomService;
import com.unocode.slowme.room.application.dto.CreateRoomRequest;
import com.unocode.slowme.room.application.dto.JoinLeaveRoomRequest;
import com.unocode.slowme.room.domain.RoomStatus;
import com.unocode.slowme.room.domain.RoomType;
import com.unocode.slowme.room.infra.RoomEntity;
import com.unocode.slowme.room.infra.RoomJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RoomConcurrencyTests {

	@Autowired
	RoomService roomService;

	@Autowired
	RoomJpaRepository roomRepo;

	@Test
	void concurrentJoin_neverExceedsCapacity() throws Exception {
		// max=3, host auto-joins => remaining seats = 2
		int max = 3;
		var create = roomService.createRoom(new CreateRoomRequest(RoomType.TYPING, 1L, max));
		Long roomId = create.id();

		int threads = 10;
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		CountDownLatch ready = new CountDownLatch(threads);
		CountDownLatch start = new CountDownLatch(1);

		AtomicInteger success = new AtomicInteger(0);
		AtomicInteger conflict = new AtomicInteger(0);

		List<Future<?>> futures = new ArrayList<>();
		for (int i = 0; i < threads; i++) {
			final long userId = 100L + i;
			futures.add(pool.submit(() -> {
				ready.countDown();
				start.await(5, TimeUnit.SECONDS);
				try {
					roomService.join(roomId, new JoinLeaveRoomRequest(userId));
					success.incrementAndGet();
				} catch (SlowmeException e) {
					// 대부분 capacity conflict
					conflict.incrementAndGet();
				}
				return null;
			}));
		}

		assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
		start.countDown();

		for (Future<?> f : futures) {
			f.get(10, TimeUnit.SECONDS);
		}
		pool.shutdownNow();

		assertThat(success.get()).isEqualTo(max - 1); // host already joined
		assertThat(conflict.get()).isEqualTo(threads - (max - 1));

		RoomEntity room = roomRepo.findById(roomId).orElseThrow();
		assertThat(room.getStatus()).isEqualTo(RoomStatus.OPEN);
		assertThat(room.getCurrentParticipants()).isEqualTo(max);
	}
}


