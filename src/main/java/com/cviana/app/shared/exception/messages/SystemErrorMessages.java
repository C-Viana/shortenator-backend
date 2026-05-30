package com.cviana.app.shared.exception.messages;

public class SystemErrorMessages {
	public static final String BAD_REQUEST_DEFAULT = "The request has an error";
	public static final String FORBIDDEN_ACCESS = "You're not allowed to access this resource";
	public static final String INVALID_ACCESS_DEFAULT = "There was an error to grant the required access";
	public static final String INVALID_PAST_EXPIRATION_DATE = "The required expiration must refer to a future date";
	public static final String NOT_FOUND_DEFAULT = "The requested resource was not found";
	public static final String SERVER_ERROR_DEFAULT = "The requested operation encountered an error and could not be executed. Try again later";
	public static final String URL_EXPIRED_DEFAULT = "The request URL has expired and is no longer available";
	public static final String URL_CODE_NOT_FOUND = "No URL found with the required code";
	public static final String NO_URL_FOUND = "No URLs were found";
}
