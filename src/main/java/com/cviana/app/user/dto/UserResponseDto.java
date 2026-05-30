package com.cviana.app.user.dto;

import com.cviana.app.user.User;

public record UserResponseDto(
		String name,
		String email,
		String password
)
{
	public static UserResponseDto setResponse(User user) {
		StringBuilder ocludedEmail = new StringBuilder();
		String email = user.getEmail();
		int atIndex = email.indexOf("@");
		
		ocludedEmail.append(email.substring(0, 2));
		ocludedEmail.append("*****************");
		ocludedEmail.append( email.substring(atIndex-3, atIndex+2) );
		ocludedEmail.append("********");
		ocludedEmail.append(email.substring(email.lastIndexOf(".")));
		
		return new UserResponseDto(user.getName(), ocludedEmail.toString(), "******************");
	}
}
