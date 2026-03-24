package com.sergiplca.payment_service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "Request payload for creating a payment")
public class PaymentRequestDto {

    @NotNull(message = "amount must be present")
    @Positive(message = "amount must be greater than zero")
    @Schema(example = "49.99", description = "Payment amount")
    private BigDecimal amount;

    @NotBlank(message = "currency must be present and not be blank")
    @Schema(example = "EUR", description = "Payment currency")
    private String currency;

    @NotBlank(message = "customerReference must be present and not be blank")
    @Schema(example = "pa_1234", description = "Payment customer reference")
    private String customerReference;
}
