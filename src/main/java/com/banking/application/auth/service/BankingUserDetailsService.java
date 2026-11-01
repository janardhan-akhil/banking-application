package com.banking.application.auth.service;

import com.banking.application.auth.entity.BankingUser;
import com.banking.application.auth.repository.BankingUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class BankingUserDetailsService implements UserDetailsService {

    private final BankingUserRepository userRepository;

    public BankingUserDetailsService(BankingUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        BankingUser user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        return User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .disabled(!user.isEnabled())
                .build();
    }
}