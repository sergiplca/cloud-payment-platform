package com.sergiplca.api_gateway.service;

import com.sergiplca.api_gateway.configuration.TokenProperties;
import com.sergiplca.api_gateway.model.dto.TokenRequestDto;
import com.sergiplca.api_gateway.model.dto.TokenResponseDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProperties tokenProperties;

    public TokenResponseDto createToken(TokenRequestDto tokenRequestDto) {

        var expiration = new Date(System.currentTimeMillis() + tokenProperties.getExpiration());

        var token = Jwts.builder()
            .subject(tokenRequestDto.getUsername())
            .claim("roles", List.of("USER"))
            .issuedAt(new Date())
            .expiration(expiration)
            .signWith(Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
            .compact();

        return TokenResponseDto.builder()
            .accessToken(token)
            .tokenType("Bearer")
            .expiresIn(tokenProperties.getExpiration())
            .build();
    }
}
