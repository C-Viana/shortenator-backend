package com.cviana.app.user.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDto(
		@NotBlank
		@Length(min = 6, max = 100)
		String name,
		@NotBlank
		@Email
		@Length(min = 15, max = 150)
		String email,
		@NotBlank
		@Length(min = 8, max = 255)
		String password
)
{}
