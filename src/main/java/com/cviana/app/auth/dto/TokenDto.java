package com.cviana.app.auth.dto;

import java.time.LocalDateTime;

public record TokenDto(
		String tokenType,
		String accessToken,
		String refreshToken,
		LocalDateTime expiration
)
{}
