
package com.banking.application.account.dto;

import com.banking.application.account.entity.AccountStatus;
import com.banking.application.account.entity.AccountType;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
        Long id,
        String accountNumber,
        Long customerId,
        AccountType accountType,
        BigDecimal balance,
        String currency,
        AccountStatus status,
        Instant createdAt
) {
}
