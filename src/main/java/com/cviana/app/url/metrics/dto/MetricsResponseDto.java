package com.cviana.app.url.metrics.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

public record MetricsResponseDto(
	Long totalAccesses,
    Map<LocalDate, Long> accessesPerDay,
    Map<String, Long> accessesByDevice,
    Map<String, Long> accessesByReferrer,
    Instant lastAccessedAt
) {}
