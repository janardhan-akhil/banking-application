
package com.banking.application.account.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "bank_accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_bank_accounts_account_number",
                        columnNames = "account_number"
                )
        },
        indexes = {
                @Index(
                        name = "idx_bank_accounts_customer_id",
                        columnList = "customer_id"
                )
        }
)
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, length = 12, unique = true)
    private String accountNumber;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false, length = 3)
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BankAccount() {
    }

    public BankAccount(
            String accountNumber,
            Long customerId,
            AccountType accountType) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = BigDecimal.ZERO;
        this.currency = "INR";
        this.status = AccountStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void freeze() {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Only active accounts can be frozen"
            );
        }
        status = AccountStatus.FROZEN;
    }

    public void unfreeze() {
        if (status != AccountStatus.FROZEN) {
            throw new IllegalStateException(
                    "Only frozen accounts can be reactivated"
            );
        }
        status = AccountStatus.ACTIVE;
    }

    public void close() {
        if (status != AccountStatus.ACTIVE
                && status != AccountStatus.FROZEN) {
            throw new IllegalStateException(
                    "Account is already closed"
            );
        }

        if (balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException(
                    "Account balance must be zero before closing"
            );
        }

        status = AccountStatus.CLOSED;
    }
}
