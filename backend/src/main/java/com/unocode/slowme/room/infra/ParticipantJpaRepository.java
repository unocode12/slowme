package com.unocode.slowme.room.infra;

import java.util.Optional;

import com.unocode.slowme.room.domain.ParticipantStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipantJpaRepository extends JpaRepository<ParticipantEntity, Long> {
	boolean existsByRoomIdAndUserIdAndStatus(Long roomId, Long userId, ParticipantStatus status);

	Optional<ParticipantEntity> findFirstByRoomIdAndUserIdAndStatus(Long roomId, Long userId, ParticipantStatus status);

	@Modifying(flushAutomatically = true)
	@Query("""
			update ParticipantEntity p
			set p.status = com.unocode.slowme.room.domain.ParticipantStatus.JOINED
			where p.roomId = :roomId
			  and p.userId = :userId
			  and p.status = com.unocode.slowme.room.domain.ParticipantStatus.LEFT
			""")
	int markJoinedIfLeft(@Param("roomId") Long roomId, @Param("userId") Long userId);

	@Modifying(flushAutomatically = true)
	@Query("""
			update ParticipantEntity p
			set p.status = com.unocode.slowme.room.domain.ParticipantStatus.LEFT
			where p.roomId = :roomId
			  and p.userId = :userId
			  and p.status = com.unocode.slowme.room.domain.ParticipantStatus.JOINED
			""")
	int markLeftIfJoined(@Param("roomId") Long roomId, @Param("userId") Long userId);
}


