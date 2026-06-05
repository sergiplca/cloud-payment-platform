package com.sergiplca.order_service.service;

import com.sergiplca.order_service.exception.NotFoundException;
import com.sergiplca.order_service.model.dto.OrderRequestDto;
import com.sergiplca.order_service.model.dto.OrderResponseDto;
import com.sergiplca.order_service.model.enums.OrderStatus;
import com.sergiplca.order_service.repository.OrderRepository;
import com.sergiplca.order_service.service.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {

        log.info("Creating order");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        var order = orderMapper.toEntity(requestDto);
        order.setUserId(jwt.getClaim("userId"));
        order.setStatus(OrderStatus.CREATED);

        var savedOrder = orderRepository.save(order);

        log.info("Order with id {} created", savedOrder.getId());

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional
    public OrderResponseDto getOrder(Long orderId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();

        return orderMapper.toResponse(orderRepository.findByIdAndUserId(orderId, jwt.getClaim("userId"))
            .orElseThrow(() -> new NotFoundException("Order with id " + orderId + " not found")));
    }
}
