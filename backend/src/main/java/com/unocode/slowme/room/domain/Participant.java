package com.unocode.slowme.room.domain;

public record Participant(
		Long userId,
		Long roomId,
		ParticipantStatus status
) {
}


