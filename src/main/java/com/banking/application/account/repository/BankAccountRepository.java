
package com.banking.application.account.repository;

import com.banking.application.account.entity.AccountType;
import com.banking.application.account.entity.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankAccountRepository
        extends JpaRepository<BankAccount, Long> {

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    Page<BankAccount> findByCustomerId(
            Long customerId,
            Pageable pageable
    );

    Optional<BankAccount> findByIdAndCustomerId(
            Long id,
            Long customerId
    );

    boolean existsByCustomerIdAndAccountType(
            Long customerId,
            AccountType accountType);
}
