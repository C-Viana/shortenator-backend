package com.cviana.app.shared.exception.errors;

import com.cviana.app.shared.exception.messages.SystemErrorMessages;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 4861906542535641965L;
	
	public NotFoundException() {
		super(SystemErrorMessages.NOT_FOUND_DEFAULT);
	}
	
	public NotFoundException(String message) {
		super(message);
	}
}
