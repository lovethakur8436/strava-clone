package com.fitness.identity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.identity.dto.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

// 1. @SpringBootTest: Tells Spring to boot up the ENTIRE application (Controllers, Services, DB connections) just like in production.
// 2. @AutoConfigureMockMvc: Wires up the fake network server for us to send requests.
// 3. @ActiveProfiles("test"): Tells Spring to look for application-test.yml so we don't touch our main database.
// 4. @Transactional: Guarantees everything saved in this test is erased from Postgres the millisecond the test finishes.

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // Our fake Postman

    @Autowired
    private ObjectMapper objectMapper; // Spring's tool to convert Java Objects to JSON strings

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRegisterUserAndReturnJwt() throws Exception {
        // Arrange: Create our DTO
        UserRegistrationRequest request = new UserRegistrationRequest(
                "test@strava-clone.com", "securePassword123", "Luv", "Kumar");

        // Act & Assert (Network Layer): Send the request and check the HTTP response
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated()) // Expect
                                                                                                                   // 201
                .andExpect(
                        org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.token").exists()); // Expect
                                                                                                                         // a
                                                                                                                         // JWT
                                                                                                                         // in
                                                                                                                         // the
                                                                                                                         // response

        // Assert (Database Layer): Prove the database actually stored the data
        boolean userExists = userRepository.findByEmail("test@strava-clone.com").isPresent();
        assert (userExists);
    }
}