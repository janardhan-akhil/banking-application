package com.banking.application.auth.dto;

import java.time.Instant;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt
) {
}