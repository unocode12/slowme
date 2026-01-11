package com.unocode.slowme.common.error.code;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	INVALID_REQUEST("COMMON-400", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	UNAUTHORIZED("COMMON-401", HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
	FORBIDDEN("COMMON-403", HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
	NOT_FOUND("COMMON-404", HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
	CONFLICT("COMMON-409", HttpStatus.CONFLICT, "요청이 충돌했습니다."),
	INTERNAL_ERROR("COMMON-500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

	private final String code;
	private final HttpStatus httpStatus;
	private final String defaultMessage;

	ErrorCode(String code, HttpStatus httpStatus, String defaultMessage) {
		this.code = code;
		this.httpStatus = httpStatus;
		this.defaultMessage = defaultMessage;
	}

	public String code() {
		return code;
	}

	public HttpStatus httpStatus() {
		return httpStatus;
	}

	public String defaultMessage() {
		return defaultMessage;
	}
}


