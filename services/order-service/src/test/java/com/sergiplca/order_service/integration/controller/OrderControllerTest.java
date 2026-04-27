package com.sergiplca.order_service.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiplca.order_service.integration.configuration.AbstractPostgresTest;
import com.sergiplca.order_service.integration.configuration.IntegrationTest;
import com.sergiplca.order_service.model.enums.OrderStatus;
import com.sergiplca.order_service.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.sergiplca.order_service.fixtures.OrderFixtures.getOrderRequestDto;
import static com.sergiplca.order_service.fixtures.OrderFixtures.getOrderResponseDto;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@Sql(scripts = "/fixtures/sql/order.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/fixtures/sql/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class OrderControllerTest extends AbstractPostgresTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Test
    void givenOrderRequestDtoWhenCreateOrderThenItIsCreated() throws Exception {

        var orderRequestDto = getOrderRequestDto();

        mockMvc
            .perform(post("/v1/orders")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(orderRequestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.orderId", equalTo(1)))
            .andExpect(jsonPath("$.status", equalTo("CREATED")));
    }

    @Test
    void givenExistingOrderWhenGetThenOrderIsReturnedInTheBody() throws Exception {

        mockMvc
            .perform(get("/v1/orders/{orderId}", 3L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.orderId", equalTo(3)))
            .andExpect(jsonPath("$.status", equalTo(OrderStatus.CREATED.name())));
    }

    @Test
    void givenNonExistingOrderWhenGetThenNotFound() throws Exception {

        mockMvc
            .perform(get("/v1/orders/{orderId}", 2L))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.detail", equalTo("Order with id 2 not found")))
            .andExpect(jsonPath("$.status", equalTo(404)));
    }
}
