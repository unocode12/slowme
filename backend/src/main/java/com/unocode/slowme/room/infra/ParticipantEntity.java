package com.unocode.slowme.room.infra;

import com.unocode.slowme.room.domain.ParticipantStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
		name = "participants",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_participants_room_user", columnNames = {"roomId", "userId"})
		},
		indexes = {
				@Index(name = "idx_participants_room_id", columnList = "roomId"),
				@Index(name = "idx_participants_user_id", columnList = "userId")
		}
)
public class ParticipantEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Long roomId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ParticipantStatus status;

	protected ParticipantEntity() {
	}

	public ParticipantEntity(Long userId, Long roomId, ParticipantStatus status) {
		this.userId = userId;
		this.roomId = roomId;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public ParticipantStatus getStatus() {
		return status;
	}
}


