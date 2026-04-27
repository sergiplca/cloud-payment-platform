package com.sergiplca.payment_service.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiplca.payment_service.client.OrderClient;
import com.sergiplca.payment_service.integration.configuration.AbstractPostgresTest;
import com.sergiplca.payment_service.integration.configuration.IntegrationTest;
import com.sergiplca.payment_service.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static com.sergiplca.payment_service.fixtures.OrderFixtures.getOrderResponseDto;
import static com.sergiplca.payment_service.fixtures.PaymentFixtures.getPaymentRequestDto;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@Sql(scripts = "/fixtures/sql/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PaymentControllerTest extends AbstractPostgresTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentService paymentService;

    @MockitoBean
    private OrderClient orderClient;

    @Test
    void givenPaymentRequestDtoWhenCreatePaymentThenItIsCreated() throws Exception {

        var paymentRequestDto = getPaymentRequestDto();

        when(orderClient.getOrder(1L)).thenReturn(ResponseEntity.ok(getOrderResponseDto(1L)));

        mockMvc
            .perform(post("/v1/payments")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(paymentRequestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.paymentId", equalTo(1)))
            .andExpect(jsonPath("$.status", equalTo("CREATED")));
    }
}
