package com.sergiplca.payment_service.controller;

import com.sergiplca.payment_service.model.dto.PaymentRequestDto;
import com.sergiplca.payment_service.model.dto.PaymentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Payments", description = "Payment operations")
@SecurityRequirement(name = "bearerAuth")
public interface PaymentControllerSwaggerSpec {

    @Operation(
        summary = "Create a payment",
        description = "Creates a new payment from the supplied request data"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Payment created successfully",
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
    PaymentResponseDto createPayment(PaymentRequestDto requestDto);
}
