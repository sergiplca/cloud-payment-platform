package com.sergiplca.order_service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO to place an order")
public class OrderRequestDto {

    @NotNull(message = "amount must be present")
    @Positive(message = "amount must be greater than zero")
    @Schema(example = "49.99", description = "Order amount")
    private BigDecimal amount;

    @NotBlank(message = "currency must be present and not be blank")
    @Schema(example = "EUR", description = "Order currency")
    private String currency;

    @NotBlank(message = "customerReference must be present and not be blank")
    @Schema(example = "pa_1234", description = "Order customer reference")
    private String customerReference;
}
