package com.banking.application.auth;

import com.banking.application.auth.repository.BankingUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BankingUserRepository userRepository;

    @BeforeEach
    void cleanUsers() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterAndLogin() throws Exception {
        String request = """
            {
              "email": "customer@example.com",
              "password": "SecurePassword123"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresAt").isNotEmpty());
    }

    @Test
    void shouldRejectDuplicateRegistration() throws Exception {
        String request = """
            {
              "email": "customer@example.com",
              "password": "SecurePassword123"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldRejectInvalidPassword() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "email": "customer@example.com",
                      "password": "WrongPassword123"
                    }
                    """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/customers?page=0&size=20"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowProtectedEndpointWithValidToken() throws Exception {
        String request = """
            {
              "email": "customer@example.com",
              "password": "SecurePassword123"
            }
            """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated());

        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = com.jayway.jsonpath.JsonPath.read(
                response, "$.accessToken");

        mockMvc.perform(get("/api/v1/customers?page=0&size=20")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}