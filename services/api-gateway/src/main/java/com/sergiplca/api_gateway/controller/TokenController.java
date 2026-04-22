package com.sergiplca.api_gateway.controller;

import com.sergiplca.api_gateway.model.dto.TokenRequestDto;
import com.sergiplca.api_gateway.model.dto.TokenResponseDto;
import com.sergiplca.api_gateway.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/token")
@RequiredArgsConstructor
public class TokenController implements TokenControllerSwaggerSpec {

    private final TokenService tokenService;

    @Override
    @PostMapping
    public TokenResponseDto createToken(@RequestBody TokenRequestDto tokenRequestDto) {

        return tokenService.createToken(tokenRequestDto);
    }
}
