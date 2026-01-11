package com.unocode.slowme.common.error.handler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import com.unocode.slowme.common.error.code.ErrorCode;
import com.unocode.slowme.common.error.exception.SlowmeException;
import com.unocode.slowme.common.error.model.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(SlowmeException.class)
	public ResponseEntity<ApiErrorResponse> handleSlowmeException(SlowmeException e, HttpServletRequest request) {
		var code = e.errorCode();
		logAtLevel(code.httpStatus(), e, request);

		return ResponseEntity.status(code.httpStatus())
				.body(build(code.code(), messageOrDefault(e), request, e.details()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
		Map<String, Object> details = new LinkedHashMap<>();
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		for (FieldError fe : e.getBindingResult().getFieldErrors()) {
			fieldErrors.put(fe.getField(), fe.getDefaultMessage());
		}
		details.put("fieldErrors", fieldErrors);

		logAtLevel(HttpStatus.BAD_REQUEST, e, request);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(build(ErrorCode.INVALID_REQUEST.code(), ErrorCode.INVALID_REQUEST.defaultMessage(), request, details));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
		Map<String, Object> details = new LinkedHashMap<>();
		Map<String, String> violations = new LinkedHashMap<>();
		e.getConstraintViolations().forEach(v ->
				violations.put(String.valueOf(v.getPropertyPath()), v.getMessage()));
		details.put("violations", violations);

		logAtLevel(HttpStatus.BAD_REQUEST, e, request);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(build(ErrorCode.INVALID_REQUEST.code(), ErrorCode.INVALID_REQUEST.defaultMessage(), request, details));
	}

	@ExceptionHandler({
			HttpMessageNotReadableException.class,
			MissingServletRequestParameterException.class,
			MethodArgumentTypeMismatchException.class
	})
	public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception e, HttpServletRequest request) {
		logAtLevel(HttpStatus.BAD_REQUEST, e, request);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(build(ErrorCode.INVALID_REQUEST.code(), ErrorCode.INVALID_REQUEST.defaultMessage(), request, Map.of()));
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiErrorResponse> handleAuthentication(AuthenticationException e, HttpServletRequest request) {
		logAtLevel(HttpStatus.UNAUTHORIZED, e, request);
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(build(ErrorCode.UNAUTHORIZED.code(), ErrorCode.UNAUTHORIZED.defaultMessage(), request, Map.of()));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException e, HttpServletRequest request) {
		logAtLevel(HttpStatus.FORBIDDEN, e, request);
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(build(ErrorCode.FORBIDDEN.code(), ErrorCode.FORBIDDEN.defaultMessage(), request, Map.of()));
	}

	@ExceptionHandler({NoResourceFoundException.class})
	public ResponseEntity<ApiErrorResponse> handleNotFound(Exception e, HttpServletRequest request) {
		logAtLevel(HttpStatus.NOT_FOUND, e, request);
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(build(ErrorCode.NOT_FOUND.code(), ErrorCode.NOT_FOUND.defaultMessage(), request, Map.of()));
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
		logAtLevel(status, e, request);
		return ResponseEntity.status(status)
				.body(build("HTTP-" + status.value(), e.getReason() != null ? e.getReason() : status.getReasonPhrase(), request, Map.of()));
	}

	@ExceptionHandler(ErrorResponseException.class)
	public ResponseEntity<ApiErrorResponse> handleErrorResponseException(ErrorResponseException e, HttpServletRequest request) {
		HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
		logAtLevel(status, e, request);
		return ResponseEntity.status(status)
				.body(build("HTTP-" + status.value(), status.getReasonPhrase(), request, Map.of()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception e, HttpServletRequest request) {
		logAtLevel(HttpStatus.INTERNAL_SERVER_ERROR, e, request);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(build(ErrorCode.INTERNAL_ERROR.code(), ErrorCode.INTERNAL_ERROR.defaultMessage(), request, Map.of()));
	}

	private static ApiErrorResponse build(String code, String message, HttpServletRequest request, Map<String, Object> details) {
		return new ApiErrorResponse(
				code,
				message,
				request.getRequestURI(),
				Instant.now(),
				MDC.get("requestId"),
				details
		);
	}

	private static String messageOrDefault(SlowmeException e) {
		return e.messageOverride() != null ? e.messageOverride() : e.errorCode().defaultMessage();
	}

	private static void logAtLevel(HttpStatus status, Exception e, HttpServletRequest request) {
		String msg = "API error: status={}, path={}, requestId={}";
		String requestId = MDC.get("requestId");
		if (status.is5xxServerError()) {
			log.error(msg, status.value(), request.getRequestURI(), requestId, e);
		} else {
			log.warn(msg, status.value(), request.getRequestURI(), requestId, e);
		}
	}
}


