package com.unocode.slowme.room.infra;

import java.time.Instant;

import com.unocode.slowme.room.domain.RoomEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
		name = "room_events",
		indexes = {
				@Index(name = "idx_room_events_room_id", columnList = "roomId"),
				@Index(name = "idx_room_events_created_at", columnList = "createdAt")
		}
)
public class RoomEventEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long roomId;

	@Column
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private RoomEventType type;

	@Column(nullable = false)
	private Instant createdAt;

	protected RoomEventEntity() {
	}

	public RoomEventEntity(Long roomId, Long userId, RoomEventType type) {
		this.roomId = roomId;
		this.userId = userId;
		this.type = type;
		this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}
}


