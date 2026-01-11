package com.unocode.slowme.room.infra;

import com.unocode.slowme.room.domain.RoomType;
import com.unocode.slowme.room.domain.RoomStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
public class RoomEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private RoomType type;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private RoomStatus status;

	@Column(nullable = false)
	private Long hostId;

	@Column(nullable = false)
	private int maxParticipants;

	@Column(nullable = false)
	private int currentParticipants;

	protected RoomEntity() {
	}

	public RoomEntity(RoomType type, Long hostId, int maxParticipants) {
		this.type = type;
		this.status = RoomStatus.OPEN;
		this.hostId = hostId;
		this.maxParticipants = maxParticipants;
		this.currentParticipants = 0;
	}

	public Long getId() {
		return id;
	}

	public RoomType getType() {
		return type;
	}

	public RoomStatus getStatus() {
		return status;
	}

	public void close() {
		this.status = RoomStatus.CLOSED;
	}

	public Long getHostId() {
		return hostId;
	}

	public int getMaxParticipants() {
		return maxParticipants;
	}

	public int getCurrentParticipants() {
		return currentParticipants;
	}
}


