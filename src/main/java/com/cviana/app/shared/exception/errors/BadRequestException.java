package com.cviana.app.shared.exception.errors;

import com.cviana.app.shared.exception.messages.SystemErrorMessages;

public class BadRequestException extends RuntimeException {
	
	private static final long serialVersionUID = -8255243595431380246L;

	public BadRequestException() {
		super(SystemErrorMessages.BAD_REQUEST_DEFAULT);
	}
	
	public BadRequestException(String message) {
		super(message);
	}
}
