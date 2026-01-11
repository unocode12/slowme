package com.unocode.slowme.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "slowme.realtime")
public record RoomRealtimeProperties(
		boolean enabled,
		String wsEndpoint,
		String redisChannel
) {
	public static final String DEFAULT_WS_ENDPOINT = "/ws";
	public static final String DEFAULT_REDIS_CHANNEL = "slowme:room-events";

	public RoomRealtimeProperties {
		if (wsEndpoint == null || wsEndpoint.isBlank()) {
			wsEndpoint = DEFAULT_WS_ENDPOINT;
		}
		if (redisChannel == null || redisChannel.isBlank()) {
			redisChannel = DEFAULT_REDIS_CHANNEL;
		}
	}
}


