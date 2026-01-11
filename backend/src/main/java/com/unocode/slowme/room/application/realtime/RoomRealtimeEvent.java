package com.unocode.slowme.room.application.realtime;

import com.unocode.slowme.room.application.dto.RoomSummaryResponse;
import com.unocode.slowme.room.domain.RoomEventType;

public record RoomRealtimeEvent(
		RoomEventType type,
		RoomSummaryResponse room
) {
}


