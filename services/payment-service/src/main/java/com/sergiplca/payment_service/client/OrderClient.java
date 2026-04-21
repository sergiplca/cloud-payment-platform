package com.sergiplca.payment_service.client;

import com.sergiplca.payment_service.model.dto.OrderResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order", url = "${feign.order.url}", dismiss404 = true)
public interface OrderClient {

    @GetMapping("/{orderId}")
    ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long orderId);
}
