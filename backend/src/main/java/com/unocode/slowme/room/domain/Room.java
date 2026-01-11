package com.unocode.slowme.room.domain;

public record Room(
		Long id,
		RoomType type,
		Long hostId,
		int maxParticipants
) {
}


