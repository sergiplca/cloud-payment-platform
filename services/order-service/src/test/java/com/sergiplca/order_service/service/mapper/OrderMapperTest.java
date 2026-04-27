package com.sergiplca.order_service.service.mapper;

import com.sergiplca.order_service.model.dto.OrderRequestDto;
import com.sergiplca.order_service.model.dto.OrderResponseDto;
import com.sergiplca.order_service.model.entity.Order;
import com.sergiplca.order_service.model.enums.OrderStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static com.sergiplca.order_service.fixtures.OrderFixtures.getOrder;
import static com.sergiplca.order_service.fixtures.OrderFixtures.getOrderRequestDto;
import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void givenOrderRequestDtoWhenMapToOrderThenAllFieldsAreCorrect() {

        OrderRequestDto dto = getOrderRequestDto();

        Order entity = orderMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(BigDecimal.TEN, entity.getAmount());
        assertEquals("EUR", entity.getCurrency());
        assertEquals("abc", entity.getCustomerReference());
        assertNull(entity.getId());
        assertNull(entity.getStatus());
        assertNull(entity.getCreationTimestamp());
    }

    @Test
    void givenOrderWhenMapToOrderResponseDtoThenAllFieldsAreCorrect() {

        Order order = getOrder(1L);

        OrderResponseDto responseDto = orderMapper.toResponse(order);

        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getOrderId());
        assertEquals(OrderStatus.CREATED, responseDto.getStatus());
    }
}
