package com.sergiplca.payment_assistant_service.model.dto.ollama;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OllamaEmbedRequest {

    private String model;

    private String input;

    private Boolean truncate = true;

    private Integer dimensions;
}
