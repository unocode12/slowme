package com.unocode.slowme.common.error.exception;

import java.util.Collections;
import java.util.Map;

import com.unocode.slowme.common.error.code.ErrorCode;

public class SlowmeException extends RuntimeException {
	private final ErrorCode errorCode;
	private final String message;
	private final Map<String, Object> details;

	public SlowmeException(ErrorCode errorCode) {
		this(errorCode, null, null, null);
	}

	public SlowmeException(ErrorCode errorCode, String message) {
		this(errorCode, message, null, null);
	}

	public SlowmeException(ErrorCode errorCode, String message, Throwable cause) {
		this(errorCode, message, null, cause);
	}

	public SlowmeException(ErrorCode errorCode, String message, Map<String, Object> details) {
		this(errorCode, message, details, null);
	}

	public SlowmeException(ErrorCode errorCode, String message, Map<String, Object> details, Throwable cause) {
		super(message != null ? message : errorCode.defaultMessage(), cause);
		this.errorCode = errorCode;
		this.message = message;
		this.details = details != null ? Collections.unmodifiableMap(details) : Collections.emptyMap();
	}

	public ErrorCode errorCode() {
		return errorCode;
	}

	/**
	 * If null, use {@link ErrorCode#defaultMessage()}.
	 */
	public String messageOverride() {
		return message;
	}

	public Map<String, Object> details() {
		return details;
	}
}


