package com.sergiplca.order_service.service;

import com.sergiplca.order_service.exception.NotFoundException;
import com.sergiplca.order_service.model.entity.Order;
import com.sergiplca.order_service.model.enums.OrderStatus;
import com.sergiplca.order_service.repository.OrderRepository;
import com.sergiplca.order_service.service.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static com.sergiplca.order_service.fixtures.OrderFixtures.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    void givenOrderRequestDtoWhenCreateOrderThenOrderIsCreated() {

        var orderRequestDto = getOrderRequestDto();

        when(orderMapper.toEntity(orderRequestDto)).thenReturn(getOrder(1L));
        when(orderRepository.save(any())).thenReturn(getOrder(1L));

        orderService.createOrder(orderRequestDto);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());

        assertEquals(BigDecimal.TEN, orderCaptor.getValue().getAmount());
        assertEquals("EUR", orderCaptor.getValue().getCurrency());
        assertEquals("abc", orderCaptor.getValue().getCustomerReference());
        assertEquals(OrderStatus.CREATED, orderCaptor.getValue().getStatus());
    }

    @Test
    void givenOrderIdWhenFindOrderThenOrderIsRetrieved() {

        var order = getOrder(1L);
        var orderResponseDto = getOrderResponseDto(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(orderResponseDto);

        assertEquals(orderResponseDto, orderService.getOrder(1L));
    }

    @Test
    void givenOrderIdWhenFindNonExistingOrderThenNotFoundExceptionIsThrown() {

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        var ex = assertThrows(NotFoundException.class, () -> orderService.getOrder(1L));
        assertEquals("Order with id " + 1L + " not found", ex.getMessage());
    }
}
