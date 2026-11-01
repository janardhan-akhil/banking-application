
package com.banking.application.customer.dto;

import com.banking.application.customer.entity.CustomerStatus;

import java.time.Instant;

public record CustomerResponse(
        Long id,
        String customerReference,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        CustomerStatus status,
        Instant createdAt
) {}
