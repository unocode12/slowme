package com.unocode.slowme.room.infra;

import java.util.List;

import com.unocode.slowme.room.domain.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

public interface RoomJpaRepository extends JpaRepository<RoomEntity, Long> {
	List<RoomEntity> findAllByStatus(RoomStatus status);

	@Modifying(flushAutomatically = true)
	@Query("""
			update RoomEntity r
			set r.currentParticipants = r.currentParticipants + 1
			where r.id = :id
			  and r.status = com.unocode.slowme.room.domain.RoomStatus.OPEN
			  and r.currentParticipants < r.maxParticipants
			""")
	int tryIncrementParticipants(@Param("id") Long id);

	@Modifying(flushAutomatically = true)
	@Query("""
			update RoomEntity r
			set r.currentParticipants = r.currentParticipants - 1
			where r.id = :id
			  and r.currentParticipants > 0
			""")
	int decrementParticipants(@Param("id") Long id);

	@Modifying(flushAutomatically = true)
	@Query("""
			update RoomEntity r
			set r.status = com.unocode.slowme.room.domain.RoomStatus.CLOSED
			where r.id = :id
			  and r.status = com.unocode.slowme.room.domain.RoomStatus.OPEN
			""")
	int closeIfOpen(@Param("id") Long id);
}


