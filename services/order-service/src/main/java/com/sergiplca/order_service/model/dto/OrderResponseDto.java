package com.sergiplca.order_service.model.dto;

import com.sergiplca.order_service.model.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response returned after order creation")
public class OrderResponseDto {

    @Schema(example = "12345", description = "Order identifier")
    private Long orderId;

    @Schema(example = "CREATED", description = "Current order status", oneOf = OrderStatus.class)
    private OrderStatus status;
}
