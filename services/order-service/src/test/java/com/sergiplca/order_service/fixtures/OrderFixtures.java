package com.sergiplca.order_service.fixtures;

import com.sergiplca.order_service.model.dto.OrderRequestDto;
import com.sergiplca.order_service.model.dto.OrderResponseDto;
import com.sergiplca.order_service.model.entity.Order;
import com.sergiplca.order_service.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderFixtures {

   public static Order getOrder(Long orderId) {

       var order = new Order();
       order.setId(orderId);
       order.setAmount(BigDecimal.TEN);
       order.setCurrency("EUR");
       order.setCustomerReference("abc");
       order.setStatus(OrderStatus.CREATED);
       order.setCreationTimestamp(LocalDateTime.of(2026, 1, 1, 0, 0, 0));

       return order;
   }

   public static OrderRequestDto getOrderRequestDto() {

       return getOrderRequestDto(BigDecimal.TEN, "EUR", "abc");
   }

   public static OrderRequestDto getOrderRequestDto(BigDecimal amount, String currency, String customerReference) {

       var requestDto = new OrderRequestDto();
       requestDto.setAmount(amount);
       requestDto.setCurrency(currency);
       requestDto.setCustomerReference(customerReference);

       return requestDto;
   }

   public static OrderResponseDto getOrderResponseDto(Long orderId) {

       var responseDto = new OrderResponseDto();
       responseDto.setOrderId(orderId);
       responseDto.setStatus(OrderStatus.CREATED);

       return responseDto;
   }
}
