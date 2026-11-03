
package com.banking.application.account.controller;

import com.banking.application.account.dto.AccountResponse;
import com.banking.application.account.dto.CreateAccountRequest;
import com.banking.application.account.service.BankAccountService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class BankAccountController {

    private final BankAccountService accountService;

    public BankAccountController(BankAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
   @PreAuthorize("hasAnyRole('ADMIN', 'TELLER')")
    public AccountResponse createAccount(
            @Valid @RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'TELLER')")
    public AccountResponse getAccount(
            @PathVariable Long accountId) {
        return accountService.getAccount(accountId);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'TELLER')")
    public Page<AccountResponse> getCustomerAccounts(
            @PathVariable Long customerId,
            @PageableDefault(size = 20, sort = "createdAt")
            Pageable pageable) {
        return accountService.getCustomerAccounts(customerId, pageable);
    }


    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'TELLER')")
    public Page<AccountResponse> getMyAccounts(
            @AuthenticationPrincipal Jwt jwt,
            @PageableDefault(size = 20, sort = "createdAt")
            Pageable pageable) {

        return accountService.getMyAccounts(jwt.getSubject(), pageable);
    }

    @GetMapping("/me/{accountId}")
    @PreAuthorize("hasRole('CUSTOMER', 'ADMIN', 'TELLER')")
    public AccountResponse getMyAccount(
            @PathVariable Long accountId,
            @AuthenticationPrincipal Jwt jwt) {

        return accountService.getMyAccount(
                accountId, jwt.getSubject());
    }

}
