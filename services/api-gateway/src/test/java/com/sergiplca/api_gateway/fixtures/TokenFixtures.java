package com.sergiplca.api_gateway.fixtures;

import com.sergiplca.api_gateway.model.dto.TokenRequestDto;
import com.sergiplca.api_gateway.model.dto.TokenResponseDto;

import java.util.List;

public class TokenFixtures {

    public static TokenRequestDto getTokenRequestDto() {

        return getTokenRequestDto("armin", List.of("USER"));
    }

    public static TokenRequestDto getTokenRequestDto(String username, List<String> roles) {

        var dto = new TokenRequestDto();
        dto.setUsername(username);
        dto.setRoles(roles);

        return dto;
    }

    public static TokenResponseDto getTokenResponseDto() {

        var dto = new TokenResponseDto();
        dto.setAccessToken("ey1234");
        dto.setTokenType("Bearer");
        dto.setExpiresIn(1L);

        return dto;
    }
}
