package com.sergiplca.payment_service.integration.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.sergiplca.payment_service.client.OrderClient;
import com.sergiplca.payment_service.integration.configuration.AbstractIntegrationTest;
import com.sergiplca.payment_service.integration.configuration.IntegrationTest;
import com.sergiplca.payment_service.model.dto.OrderResponseDto;
import com.sergiplca.payment_service.model.enums.OrderStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@IntegrationTest
class OrderClientTest extends AbstractIntegrationTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private OrderClient orderClient;

    @BeforeAll
    static void setUp() {

        wireMockServer = new WireMockServer(0);
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void tearDown() {

        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {

        registry.add("feign.order.url", () -> wireMockServer.baseUrl());
    }

    @Test
    void givenOrderIdWhenGetThenOrderIsRetrieved() {

        stubFor(get(urlEqualTo("/1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "orderId": 1,
                      "orderStatus": "CREATED"
                    }
                """)));

        var response = orderClient.getOrder(1L);
        OrderResponseDto body = response.getBody();

        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(body);
        assertEquals(1L, body.getOrderId());
        assertEquals(OrderStatus.CREATED, body.getOrderStatus());
    }

    @Test
    void givenOrderIdWhenGetThenOrderIsNotFound() {

        stubFor(get(urlEqualTo("/999")).willReturn(aResponse().withStatus(404)));

        var response = orderClient.getOrder(999L);

        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
