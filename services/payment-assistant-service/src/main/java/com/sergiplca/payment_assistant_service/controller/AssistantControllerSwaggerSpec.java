package com.sergiplca.payment_assistant_service.controller;

import com.sergiplca.payment_assistant_service.model.dto.AssistantRequestDto;
import com.sergiplca.payment_assistant_service.model.dto.AssistantResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Assistant", description = "Natural Language Assistant")
@SecurityRequirement(name = "bearerAuth")
public interface AssistantControllerSwaggerSpec {

    @Operation(
        summary = "Ask a question to the RAG payment assistant",
        description = "Natural language assistant"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Question answered successfully",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request payload",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid access token",
            content = @Content
        )
    })
    AssistantResponseDto query(AssistantRequestDto requestDto);
}
