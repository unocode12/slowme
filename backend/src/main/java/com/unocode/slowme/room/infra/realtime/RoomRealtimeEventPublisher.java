package com.unocode.slowme.room.infra.realtime;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.unocode.slowme.config.properties.RoomRealtimeProperties;
import com.unocode.slowme.room.application.realtime.RoomRealtimeEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "slowme.realtime.enabled", havingValue = "true")
public class RoomRealtimeEventPublisher implements com.unocode.slowme.room.application.port.out.RoomRealtimeEventPublisher {
	private static final Logger log = LoggerFactory.getLogger(RoomRealtimeEventPublisher.class);

	private final StringRedisTemplate redis;
	private final ObjectMapper objectMapper;
	private final RoomRealtimeProperties props;

	@Override
	public void publish(RoomRealtimeEvent event) {
		try {
			String json = objectMapper.writeValueAsString(event);
			// Redis 채널은 방 단위로 분리: <prefix>:<roomId>
			redis.convertAndSend(props.redisChannel() + ":" + event.room().id(), json);
		} catch (JacksonException e) {
			log.warn("Failed to serialize RoomRealtimeEvent", e);
		} catch (Exception e) {
			// Redis down should not break core flow
			log.warn("Failed to publish RoomRealtimeEvent to Redis", e);
		}
	}
}



