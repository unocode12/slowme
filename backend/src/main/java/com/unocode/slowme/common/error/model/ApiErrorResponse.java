package com.unocode.slowme.common.error.model;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
		String code,
		String message,
		String path,
		Instant timestamp,
		String requestId,
		Map<String, Object> details
) {
}


