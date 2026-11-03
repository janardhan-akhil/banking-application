
package com.banking.application.account.dto;

import com.banking.application.account.entity.AccountType;
import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(
        @NotNull(message = "Customer ID is required")
        Long customerId,

        @NotNull(message = "Account type is required")
        AccountType accountType
) {
}
