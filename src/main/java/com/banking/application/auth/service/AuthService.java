
package com.banking.application.auth.service;

import com.banking.application.auth.dto.AuthResponse;
import com.banking.application.auth.dto.LoginRequest;
import com.banking.application.auth.dto.RegisterRequest;
import com.banking.application.auth.entity.BankingRole;
import com.banking.application.auth.entity.BankingUser;
import com.banking.application.auth.repository.BankingUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
public class AuthService {

    private final BankingUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final Duration tokenTtl;

    public AuthService(
            BankingUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.issuer}") String issuer,
            @Value("${security.jwt.access-token-ttl}") Duration tokenTtl) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.tokenTtl = tokenTtl;
    }

    @Transactional
    public void register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateBankingUserException();
        }

        BankingUser user = new BankingUser(
                email,
                passwordEncoder.encode(request.password()),
                BankingRole.CUSTOMER
        );

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        Authentication authentication =
                authenticationManager.authenticate(
                        UsernamePasswordAuthenticationToken.unauthenticated(
                                email, request.password()));

        Instant now = Instant.now();
        Instant expiresAt = now.plus(tokenTtl);

        BankingUser user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user was not found"));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("roles", List.of(user.getRole().name()))
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        String token = jwtEncoder.encode(
                        JwtEncoderParameters.from(header, claims))
                .getTokenValue();

        return new AuthResponse(token, "Bearer", expiresAt);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
