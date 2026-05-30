package com.cviana.app.url.dto;

import java.time.Instant;
import java.util.List;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cviana.app.url.Url;

public record UrlResponseDto(
		String nameUrl, 
		String sourceDomain, 
		String sourceUrl, 
		String shortenedUrl,
		Instant expiresAt
)
{

	public static UrlResponseDto toResponse(Url url) {
		return new UrlResponseDto(url.getName(), url.getSourceDomain(), url.getSourceUrl(),
				(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString()) + "/" + url.getShortenedUrlCode(),
				url.getExpiresAt());
	}

	public static List<UrlResponseDto> toResponse(List<Url> url) {
		List<UrlResponseDto> result = List.of();

		for (Url item : url) {
			result.add(new UrlResponseDto(item.getName(), item.getSourceDomain(), item.getSourceUrl(),
					(ServletUriComponentsBuilder.fromCurrentContextPath().toUriString()) + "/"
							+ item.getShortenedUrlCode(),
					item.getExpiresAt()));
		}

		return result;
	}
	
}
