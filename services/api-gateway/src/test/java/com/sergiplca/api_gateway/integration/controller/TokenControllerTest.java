package com.sergiplca.api_gateway.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiplca.api_gateway.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.sergiplca.api_gateway.fixtures.TokenFixtures.getTokenRequestDto;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class TokenControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Test
    void givenTokenRequestDtoWhenCreateTokenThenItIsCreated() throws Exception {

        var tokenRequestDto = getTokenRequestDto();

        mockMvc
            .perform(post("/auth/token")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(tokenRequestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken", isA(String.class)))
            .andExpect(jsonPath("$.tokenType", equalTo("Bearer")))
            .andExpect(jsonPath("$.expiresIn", equalTo(60000)));
    }
}
