package com.sergiplca.order_service.controller;

import com.sergiplca.order_service.model.dto.OrderRequestDto;
import com.sergiplca.order_service.model.dto.OrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Orders", description = "Order operations")
@SecurityRequirement(name = "bearerAuth")
public interface OrderControllerSwaggerSpec {

    @Operation(
        summary = "Create an order",
        description = "Creates a new order from the supplied request data"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Order created successfully",
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
    OrderResponseDto createOrder(OrderRequestDto requestDto);

    @Operation(
        summary = "Find an order by id",
        description = "Given an order id, the details about the order are returned"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Order retrieved successfully",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Order with given id not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Invalid access token",
            content = @Content
        )
    })
    OrderResponseDto getOrder(Long orderId);
}
