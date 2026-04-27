package com.sergiplca.payment_service.fixtures;

import com.sergiplca.payment_service.model.dto.OrderResponseDto;
import com.sergiplca.payment_service.model.enums.OrderStatus;

public class OrderFixtures {

    public static OrderResponseDto getOrderResponseDto(Long orderId) {

        var responseDto = new OrderResponseDto();
        responseDto.setOrderId(orderId);
        responseDto.setOrderStatus(OrderStatus.CREATED);

        return responseDto;
    }
}
