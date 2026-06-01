package com.sergiplca.payment_assistant_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PaymentAssistantServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentAssistantServiceApplication.class, args);
	}

}
