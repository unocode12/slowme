package com.unocode.slowme.room.infra.realtime;

import tools.jackson.databind.ObjectMapper;
import com.unocode.slowme.config.properties.RoomRealtimeProperties;
import com.unocode.slowme.room.application.realtime.RoomRealtimeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
@ConditionalOnProperty(name = "slowme.realtime.enabled", havingValue = "true")
public class RoomRealtimeSubscriberConfig {
	private static final Logger log = LoggerFactory.getLogger(RoomRealtimeSubscriberConfig.class);

	@Bean
	RedisMessageListenerContainer roomEventListenerContainer(
			RedisConnectionFactory connectionFactory,
			MessageListenerAdapter roomEventListenerAdapter,
			PatternTopic roomEventTopic
	) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(roomEventListenerAdapter, roomEventTopic);
		return container;
	}

	@Bean
	PatternTopic roomEventTopic(RoomRealtimeProperties props) {
		// 방 단위 채널을 모두 구독: <prefix>:*
		return new PatternTopic(props.redisChannel() + ":*");
	}

	@Bean
	MessageListenerAdapter roomEventListenerAdapter(RoomEventRedisListener listener) {
		return new MessageListenerAdapter(listener, "onMessage");
	}

	@Bean
	RoomEventRedisListener roomEventRedisListener(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
		return new RoomEventRedisListener(messagingTemplate, objectMapper);
	}

	public static class RoomEventRedisListener {
		private final SimpMessagingTemplate messagingTemplate;
		private final ObjectMapper objectMapper;

		public RoomEventRedisListener(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
			this.messagingTemplate = messagingTemplate;
			this.objectMapper = objectMapper;
		}

		@SuppressWarnings("unused")
		public void onMessage(String message) {
			try {
				RoomRealtimeEvent event = objectMapper.readValue(message, RoomRealtimeEvent.class);

				// global list updates
				messagingTemplate.convertAndSend("/topic/rooms", event);
				// room detail updates
				messagingTemplate.convertAndSend("/topic/rooms." + event.room().id(), event);
			} catch (Exception e) {
				log.warn("Failed to handle room event from redis: {}", message, e);
			}
		}
	}
}


