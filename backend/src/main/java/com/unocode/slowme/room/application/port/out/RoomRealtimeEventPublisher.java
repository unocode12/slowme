package com.unocode.slowme.room.application.port.out;

import com.unocode.slowme.room.application.realtime.RoomRealtimeEvent;

public interface RoomRealtimeEventPublisher {
	void publish(RoomRealtimeEvent event);
}


