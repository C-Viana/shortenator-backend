package com.cviana.app.url.dto;

import java.time.LocalDateTime;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record UrlRequestDto(
		@NotBlank
		@Length(min = 6, max = 100)
		String nameUrl,
		@NotBlank
		@URL
		String sourceUrl,
		@Nullable
		@DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm")
		LocalDateTime expiresAt
) {}
