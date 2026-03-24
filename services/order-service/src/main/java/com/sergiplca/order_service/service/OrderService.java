package com.sergiplca.order_service.service;

import com.sergiplca.order_service.model.dto.OrderRequestDto;
import com.sergiplca.order_service.model.dto.OrderResponseDto;
import com.sergiplca.order_service.model.enums.OrderStatus;
import com.sergiplca.order_service.repository.OrderRepository;
import com.sergiplca.order_service.service.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderResponseDto createOrder(OrderRequestDto requestDto) {

        log.info("Creating order");

        var order = orderMapper.toEntity(requestDto);
        order.setStatus(OrderStatus.CREATED);

        var savedOrder = orderRepository.save(order);

        log.info("Order with id {} created", savedOrder.getId());

        return orderMapper.toResponse(savedOrder);
    }
}
