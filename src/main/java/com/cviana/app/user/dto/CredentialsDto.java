package com.cviana.app.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CredentialsDto(
		@NotNull
		@NotEmpty
		@NotBlank
		String email,
		@NotNull
		@NotEmpty
		@NotBlank
		String password
)
{}
