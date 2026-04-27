package com.sergiplca.order_service.service;

import com.sergiplca.order_service.exception.NotFoundException;
import com.sergiplca.order_service.repository.OrderRepository;
import com.sergiplca.order_service.service.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.sergiplca.order_service.fixtures.OrderFixtures.getOrder;
import static com.sergiplca.order_service.fixtures.OrderFixtures.getOrderResponseDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
