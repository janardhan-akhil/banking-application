package com.banking.application.auth.repository;

import com.banking.application.auth.entity.BankingUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankingUserRepository
        extends JpaRepository<BankingUser, Long> {

    Optional<BankingUser> findByEmail(String email);

    boolean existsByEmail(String email);
}