package com.sergiplca.payment_assistant_service.service;

import com.sergiplca.payment_assistant_service.client.LLMClient;
import com.sergiplca.payment_assistant_service.model.dto.event.PaymentEventDto;
import com.sergiplca.payment_assistant_service.model.dto.ollama.OllamaEmbedRequest;
import com.sergiplca.payment_assistant_service.model.dto.ollama.OllamaEmbedResponse;
import com.sergiplca.payment_assistant_service.model.entity.Embedding;
import com.sergiplca.payment_assistant_service.repository.EmbeddingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.sergiplca.payment_assistant_service.util.EmbeddingUtils.toFloatArray;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

    private final LLMClient llmClient;
    private final EmbeddingRepository embeddingRepository;

    public OllamaEmbedResponse embed(String text) {

        return llmClient.embed(OllamaEmbedRequest.builder()
            .model("nomic-embed-text")
            .input(text)
            .truncate(true)
            .dimensions(7658)
            .build());
    }

    public void embedPayment(PaymentEventDto paymentEventDto) {

        var embedding = embed(paymentEventDto.toEmbeddingText());

        embeddingRepository.save(
            Embedding.builder()
                .recordType("payment")
                .recordId(paymentEventDto.getPaymentId().toString())
                .contentText(paymentEventDto.toEmbeddingText())
                .embedding(toFloatArray(embedding.getEmbeddings().get(0)))
                .createdAt(LocalDateTime.now())
                .build()
        );

        log.info("Payment with id {} was embedded and saved successfully.", paymentEventDto.getPaymentId());
    }
}
