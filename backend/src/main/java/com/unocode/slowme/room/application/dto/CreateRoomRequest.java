package com.unocode.slowme.room.application.dto;

import com.unocode.slowme.room.domain.RoomType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateRoomRequest(
		@NotNull RoomType type,
		@NotNull Long hostId,
		@Min(2) @Max(50) int maxParticipants
) {
}


