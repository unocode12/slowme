package com.unocode.slowme.room.infra.realtime;

import com.unocode.slowme.room.application.port.out.RoomRealtimeEventPublisher;
import com.unocode.slowme.room.application.realtime.RoomRealtimeEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "slowme.realtime.enabled", havingValue = "false", matchIfMissing = true)
public class NoopRoomRealtimeEventPublisher implements RoomRealtimeEventPublisher {
	@Override
	public void publish(RoomRealtimeEvent event) {
		// no-op
	}
}


