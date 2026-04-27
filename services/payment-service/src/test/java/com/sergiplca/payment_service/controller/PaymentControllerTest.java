package com.sergiplca.payment_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sergiplca.payment_service.model.dto.PaymentRequestDto;
import com.sergiplca.payment_service.service.PaymentService;
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

import static com.sergiplca.payment_service.fixtures.PaymentFixtures.getPaymentRequestDto;
import static com.sergiplca.payment_service.fixtures.PaymentFixtures.getPaymentResponseDto;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void givenPaymentRequestDtoWhenCreatePaymentThenItIsCreated() throws Exception {

        var paymentRequestDto = getPaymentRequestDto();

        when(paymentService.createPayment(paymentRequestDto)).thenReturn(getPaymentResponseDto(1L));

        mockMvc
            .perform(post("/v1/payments")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(paymentRequestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.paymentId", equalTo(1)))
            .andExpect(jsonPath("$.status", equalTo("CREATED")));
    }

    @ParameterizedTest
    @MethodSource("paymentRequestDtoProvider")
    void givenInvalidPaymentRequestDtoWhenCreatePaymentThenFailsWithValidationError(
        PaymentRequestDto dto, String errorPath, List<String> errorMessages) throws Exception {

        mockMvc
            .perform(post("/v1/payments")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail", equalTo("One or more fields are invalid")))
            .andExpect(jsonPath("$.status", equalTo(400)))
            .andExpect(jsonPath("$.title", equalTo("Validation failed")))
            .andExpect(jsonPath(errorPath, equalTo(errorMessages)));
    }

    private static Stream<Arguments> paymentRequestDtoProvider() {

        return Stream.of(
            Arguments.of(getPaymentRequestDto(BigDecimal.ZERO, "EUR", "abc", 1L), "$.errors.amount",
                List.of("amount must be greater than zero")),
            Arguments.of(getPaymentRequestDto(BigDecimal.TEN, "", "abc", 1L), "$.errors.currency",
                List.of("currency must be present and not be blank")),
            Arguments.of(getPaymentRequestDto(BigDecimal.TEN, "EUR", "", 1L), "$.errors.customerReference",
                List.of("customerReference must be present and not be blank")),
            Arguments.of(getPaymentRequestDto(BigDecimal.TEN, "EUR", "abc", null), "$.errors.orderId",
                List.of("orderId must be present"))
        );
    }
}
