package com.sergiplca.payment_service.model.dto;

import com.sergiplca.payment_service.model.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response returned after payment creation")
public class PaymentResponseDto {

    @Schema(example = "12345", description = "Payment identifier")
    private Long paymentId;

    @Schema(example = "CREATED", description = "Current payment status", oneOf = PaymentStatus.class)
    private PaymentStatus status;
}
