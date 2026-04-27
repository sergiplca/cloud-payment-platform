package com.sergiplca.order_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiplca.order_service.exception.NotFoundException;
import com.sergiplca.order_service.model.dto.OrderRequestDto;
import com.sergiplca.order_service.model.enums.OrderStatus;
import com.sergiplca.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static com.sergiplca.order_service.fixtures.OrderFixtures.getOrderRequestDto;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.sergiplca.order_service.fixtures.OrderFixtures.getOrderResponseDto;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void givenOrderRequestDtoWhenCreateOrderThenItIsCreated() throws Exception {

        var orderRequestDto = getOrderRequestDto();

        when(orderService.createOrder(orderRequestDto)).thenReturn(getOrderResponseDto(1L));

        mockMvc
            .perform(post("/v1/orders")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(orderRequestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderId", equalTo(1)))
            .andExpect(jsonPath("$.status", equalTo("CREATED")));
    }

    @ParameterizedTest
    @MethodSource("orderRequestDtoProvider")
    void givenInvalidOrderRequestDtoWhenCreateOrderThenFailsWithValidationError(
        OrderRequestDto dto, String errorPath, List<String> errorMessages) throws Exception {

        mockMvc
            .perform(post("/v1/orders")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail", equalTo("One or more fields are invalid")))
            .andExpect(jsonPath("$.status", equalTo(400)))
            .andExpect(jsonPath("$.title", equalTo("Validation failed")))
            .andExpect(jsonPath(errorPath, equalTo(errorMessages)));
    }

    private static Stream<Arguments> orderRequestDtoProvider() {

        return Stream.of(
            Arguments.of(getOrderRequestDto(BigDecimal.ZERO, "EUR", "abc"), "$.errors.amount",
                List.of("amount must be greater than zero")),
            Arguments.of(getOrderRequestDto(BigDecimal.TEN, "", "abc"), "$.errors.currency",
                List.of("currency must be present and not be blank")),
            Arguments.of(getOrderRequestDto(BigDecimal.TEN, "EUR", ""), "$.errors.customerReference",
                List.of("customerReference must be present and not be blank"))
        );
    }

    @Test
    void givenExistingOrderWhenGetThenOrderIsReturnedInTheBody() throws Exception {

        when(orderService.getOrder(1L)).thenReturn(getOrderResponseDto(1L));

        mockMvc
            .perform(get("/v1/orders/{orderId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId", equalTo(1)))
            .andExpect(jsonPath("$.status", equalTo("CREATED")));
    }

    @Test
    void givenNonExistingOrderWhenGetThenNotFound() throws Exception {

        when(orderService.getOrder(1L)).thenThrow(new NotFoundException("Order with id 1 not found"));

        mockMvc
            .perform(get("/v1/orders/{orderId}", 1L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail", equalTo("Order with id 1 not found")))
            .andExpect(jsonPath("$.status", equalTo(404)));
    }
}
