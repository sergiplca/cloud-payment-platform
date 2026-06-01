package com.sergiplca.payment_assistant_service.client;

import com.sergiplca.payment_assistant_service.model.dto.ollama.OllamaEmbedRequest;
import com.sergiplca.payment_assistant_service.model.dto.ollama.OllamaEmbedResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "llm", url = "${feign.llm.url}")
public interface LLMClient {

    @PostMapping(value = "/api/embed", consumes = "application/json")
    OllamaEmbedResponse embed(@RequestBody OllamaEmbedRequest request);
}
