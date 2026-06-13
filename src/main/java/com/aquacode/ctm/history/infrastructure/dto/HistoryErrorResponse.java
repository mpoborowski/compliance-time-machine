package com.aquacode.ctm.history.infrastructure.dto;

import lombok.Builder;

@Builder
public record HistoryErrorResponse(
    String code,
    String message
) {
}