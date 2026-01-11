package com.unocode.slowme.room.application.dto;

import jakarta.validation.constraints.NotNull;

public record JoinLeaveRoomRequest(
		@NotNull Long userId
) {
}


