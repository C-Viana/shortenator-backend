package com.cviana.app.shared.exception.errors;

import com.cviana.app.shared.exception.messages.SystemErrorMessages;

public class InvalidAccessException extends RuntimeException {
	
	private static final long serialVersionUID = -7262046792628080047L;

	public InvalidAccessException() {
		super(SystemErrorMessages.INVALID_ACCESS_DEFAULT);
	}
	
	public InvalidAccessException(String message) {
		super(message);
	}
}
