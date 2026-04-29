package com.sergiplca.api_gateway.service;

import com.sergiplca.api_gateway.configuration.TokenProperties;
import com.sergiplca.api_gateway.model.dto.TokenRequestDto;
import com.sergiplca.api_gateway.model.dto.TokenResponseDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private final String SECRET = "my-super-secret-key-that-is-long-enough-123456";
    private final long EXPIRATION = 3600000;

    private TokenService tokenService;

    @BeforeEach
    void setUp() {

        var tokenProperties = new TokenProperties();
        tokenProperties.setSecret(SECRET);
        tokenProperties.setExpiration(EXPIRATION);

        tokenService = new TokenService(tokenProperties);
    }

    @Test
    void givenUsernameAndRolesWhenCreateTokenThenTokenIsCorrectlyReturned() {

        var request = new TokenRequestDto();
        request.setUsername("armin");

        TokenResponseDto response = tokenService.createToken(request);

        var claims = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
            .build()
            .parseSignedClaims(response.getAccessToken())
            .getPayload();

        assertNotNull(response);
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(EXPIRATION);

        assertThat(claims.getSubject()).isEqualTo("armin");
        assertThat(claims.get("roles")).isNotNull();
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }
}
