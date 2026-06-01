package com.sergiplca.payment_assistant_service.model.dto.ollama;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OllamaEmbedResponse {

    private String model;

    private List<List<Double>> embeddings;

    private Long totalDuration;

    private Long loadDuration;

    private Integer promptEvalCount;
}
