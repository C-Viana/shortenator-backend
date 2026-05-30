package com.cviana.app.shared.exception.errors;

import com.cviana.app.shared.exception.messages.SystemErrorMessages;

public class UrlExpiredException extends RuntimeException {
	
	private static final long serialVersionUID = 2462035283532099741L;

	public UrlExpiredException() {
		super(SystemErrorMessages.URL_EXPIRED_DEFAULT);
	}
	
	public UrlExpiredException(String message) {
		super(message);
	}
}
