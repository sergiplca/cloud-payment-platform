package com.sergiplca.api_gateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiplca.api_gateway.model.dto.TokenRequestDto;
import com.sergiplca.api_gateway.service.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Stream;

import static com.sergiplca.api_gateway.fixtures.TokenFixtures.getTokenRequestDto;
import static com.sergiplca.api_gateway.fixtures.TokenFixtures.getTokenResponseDto;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TokenController.class)
class TokenControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @Test
    void givenTokenRequestDtoWhenCreateTokenThenItIsCreated() throws Exception {

        var tokenRequestDto = getTokenRequestDto();

        when(tokenService.createToken(tokenRequestDto)).thenReturn(getTokenResponseDto());

        mockMvc
            .perform(post("/auth/token")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(tokenRequestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken", equalTo("ey1234")))
            .andExpect(jsonPath("$.tokenType", equalTo("Bearer")))
            .andExpect(jsonPath("$.expiresIn", equalTo(1)));
    }

    @ParameterizedTest
    @MethodSource("orderRequestDtoProvider")
    void givenInvalidOrderRequestDtoWhenCreateOrderThenFailsWithValidationError(
        TokenRequestDto dto, String errorPath, List<String> errorMessages) throws Exception {

        mockMvc
            .perform(post("/auth/token")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail", equalTo("One or more fields are invalid")))
            .andExpect(jsonPath("$.status", equalTo(400)))
            .andExpect(jsonPath("$.title", equalTo("Validation failed")))
            .andExpect(jsonPath(errorPath, containsInAnyOrder(errorMessages.toArray())));
    }

    private static Stream<Arguments> orderRequestDtoProvider() {

        return Stream.of(
            Arguments.of(getTokenRequestDto(null, List.of("USER")), "$.errors.username",
                List.of("username must be present")),
            Arguments.of(getTokenRequestDto("armin", null), "$.errors.roles",
                List.of("At least one role must be requested", "roles must be present")),
            Arguments.of(getTokenRequestDto("armin", List.of()), "$.errors.roles",
                List.of("At least one role must be requested"))
        );
    }
}
