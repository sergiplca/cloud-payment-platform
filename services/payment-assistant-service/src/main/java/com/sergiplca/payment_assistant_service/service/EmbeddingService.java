package com.sergiplca.payment_assistant_service.service;

import com.sergiplca.payment_assistant_service.client.LLMClient;
import com.sergiplca.payment_assistant_service.model.dto.event.PaymentEventDto;
import com.sergiplca.payment_assistant_service.model.dto.ollama.OllamaEmbedRequest;
import com.sergiplca.payment_assistant_service.model.entity.Embedding;
import com.sergiplca.payment_assistant_service.repository.EmbeddingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

    private final LLMClient llmClient;
    private final EmbeddingRepository embeddingRepository;

    public void embedPayment(PaymentEventDto paymentEventDto) {

        var embeddingText = paymentEventDto.toEmbeddingText();

        var embeddingRequest = OllamaEmbedRequest.builder()
            .model("nomic-embed-text")
            .input(embeddingText)
            .truncate(true)
            .dimensions(7658)
            .build();

        var embeddingResponse = llmClient.embed(embeddingRequest);

        embeddingRepository.save(
            Embedding.builder()
                .recordType("payment")
                .recordId(paymentEventDto.getPaymentId().toString())
                .contentText(embeddingText)
                .embedding(toFloatArray(embeddingResponse.getEmbeddings().get(0)))
                .createdAt(LocalDateTime.now())
                .build()
        );

        log.info("Payment with id {} was embedded and saved successfully.", paymentEventDto.getPaymentId());
    }

    private static float[] toFloatArray(List<Double> vector) {

        float[] result = new float[vector.size()];

        for (int i = 0; i < vector.size(); i++) {
            result[i] = vector.get(i).floatValue();
        }

        return result;
    }
}
