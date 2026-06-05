package com.sergiplca.api_gateway.controller;

import com.sergiplca.api_gateway.model.dto.user.UserRequestDto;
import com.sergiplca.api_gateway.model.dto.user.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User", description = "User maintenance")
public interface UserControllerSwaggerSpec {

    @Operation(
        summary = "Create a new user",
        description = "Creates a new user given a username and a password"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content
        )
    })
    UserResponseDto createToken(UserRequestDto userRequestDto);
}
