package com.cviana.app.shared.exception;

import java.time.LocalDateTime;

public record ExceptionResponseTemplate(
	String message,
	int code,
	LocalDateTime timestamp
) {}
