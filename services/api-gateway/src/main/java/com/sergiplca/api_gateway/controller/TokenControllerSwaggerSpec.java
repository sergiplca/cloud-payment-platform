package com.sergiplca.api_gateway.controller;

import com.sergiplca.api_gateway.model.dto.token.TokenRequestDto;
import com.sergiplca.api_gateway.model.dto.token.TokenResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Token", description = "Bearer token request")
public interface TokenControllerSwaggerSpec {

    @Operation(
        summary = "Request a Bearer token",
        description = "Returns a static fake token with the given username"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Token returned successfully",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found or password invalid",
            content = @Content
        )
    })
    TokenResponseDto createToken(TokenRequestDto tokenRequestDto);
}
