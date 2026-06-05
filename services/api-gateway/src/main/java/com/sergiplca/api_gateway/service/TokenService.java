package com.sergiplca.api_gateway.service;

import com.sergiplca.api_gateway.configuration.TokenProperties;
import com.sergiplca.api_gateway.exception.NotFoundException;
import com.sergiplca.api_gateway.model.dto.token.TokenRequestDto;
import com.sergiplca.api_gateway.model.dto.token.TokenResponseDto;
import com.sergiplca.api_gateway.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProperties tokenProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenResponseDto createToken(TokenRequestDto tokenRequestDto) {

        var expiration = new Date(System.currentTimeMillis() + tokenProperties.getExpiration());
        var user = userRepository.findByUsername(tokenRequestDto.getUsername());

        if (user.isPresent()
            && passwordEncoder.matches(tokenRequestDto.getPassword(), user.get().getPasswordHash())) {

            var foundUser = user.get();

            var token = Jwts.builder()
                .subject(foundUser.getUsername())
                .claim("roles", foundUser.getRoles())
                .claim("userId", foundUser.getId())
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(Keys.hmacShaKeyFor(tokenProperties.getSecret().getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                .compact();

            return TokenResponseDto.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(tokenProperties.getExpiration())
                .build();
        } else {

            throw new NotFoundException("User with username {} was not found");
        }
    }
}
