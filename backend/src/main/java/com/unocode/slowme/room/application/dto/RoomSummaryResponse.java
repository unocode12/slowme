package com.unocode.slowme.room.application.dto;

import com.unocode.slowme.room.domain.RoomStatus;
import com.unocode.slowme.room.domain.RoomType;

public record RoomSummaryResponse(
		Long id,
		RoomType type,
		RoomStatus status,
		Long hostId,
		int maxParticipants,
		long participantCount
) {
}


