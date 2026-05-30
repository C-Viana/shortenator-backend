package com.cviana.app.shared.exception.errors;

import com.cviana.app.shared.exception.messages.SystemErrorMessages;

public class ServerErrorException extends RuntimeException {

	private static final long serialVersionUID = -8462041441840914086L;

	public ServerErrorException() {
		super(SystemErrorMessages.NOT_FOUND_DEFAULT);
	}
	
	public ServerErrorException(String message) {
		super(message);
	}
}
