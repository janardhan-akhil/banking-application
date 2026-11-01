package com.banking.application.auth.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "banking_users",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_banking_users_email",
                columnNames = "email"
        )
)
public class BankingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BankingRole role;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected BankingUser() {
    }

    public BankingUser(String email, String passwordHash, BankingRole role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.enabled = true;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public BankingRole getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}