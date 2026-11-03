
package com.banking.application.account.service;

import com.banking.application.account.dto.AccountResponse;
import com.banking.application.account.dto.CreateAccountRequest;
import com.banking.application.account.entity.BankAccount;
import com.banking.application.account.repository.BankAccountRepository;
import com.banking.application.customer.entity.Customer;
import com.banking.application.customer.entity.CustomerStatus;
import com.banking.application.customer.repository.CustomerRepository;
import com.banking.application.exception.AccountNotFoundException;
import com.banking.application.exception.CustomerNotEligibleException;
import com.banking.application.exception.CustomerNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Locale;

@Service
public class BankAccountService {

    private static final int ACCOUNT_NUMBER_LENGTH = 12;
    private static final int MAX_GENERATION_ATTEMPTS = 10;

    private final BankAccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public BankAccountService(
            BankAccountRepository accountRepository,
            CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }


    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(request.customerId()));

        if (accountRepository.existsByCustomerIdAndAccountType(
                customer.getId(), request.accountType())) {
            throw new CustomerNotEligibleException(
                    "Customer already has an account of this type");
        }

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new CustomerNotEligibleException(
                    "Only active customers can open bank accounts");
        }

        String accountNumber = generateUniqueAccountNumber();

        BankAccount account = new BankAccount(
                accountNumber,
                customer.getId(),
                request.accountType()
        );

        return toResponse(accountRepository.save(account));
    }



    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long accountId) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));

        return toResponse(account);
    }


    @Transactional(readOnly = true)
    public Page<AccountResponse> getCustomerAccounts(
            Long customerId,
            Pageable pageable) {

        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found");
        }

        return accountRepository.findByCustomerId(customerId, pageable)
                .map(this::toResponse);
    }


    @Transactional(readOnly = true)
    public Page<AccountResponse> getMyAccounts(
            String authenticatedEmail,
            Pageable pageable) {

        Customer customer = findCustomerByAuthenticatedEmail(
                authenticatedEmail);

        return accountRepository
                .findByCustomerId(customer.getId(), pageable)
                .map(this::toResponse);
    }

    public AccountResponse getMyAccount(
            Long accountId,
            String authenticatedEmail) {

        Customer customer = findCustomerByAuthenticatedEmail(
                authenticatedEmail);

        BankAccount account = accountRepository
                .findByIdAndCustomerId(accountId, customer.getId())
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));

        return toResponse(account);
    }

    private Customer findCustomerByAuthenticatedEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        return customerRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer profile not found"));
    }


    private String generateUniqueAccountNumber() {
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            StringBuilder number = new StringBuilder(
                    ACCOUNT_NUMBER_LENGTH);

            number.append(1 + secureRandom.nextInt(9));

            for (int i = 1; i < ACCOUNT_NUMBER_LENGTH; i++) {
                number.append(secureRandom.nextInt(10));
            }

            String candidate = number.toString();

            if (!accountRepository.existsByAccountNumber(candidate)) {
                return candidate;
            }
        }

        throw new IllegalStateException(
                "Unable to generate a unique account number");
    }

    private AccountResponse toResponse(BankAccount account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getCustomerId(),
                account.getAccountType(),
                account.getBalance(),
                account.getCurrency(),
                account.getStatus(),
                account.getCreatedAt()
        );
    }
}
