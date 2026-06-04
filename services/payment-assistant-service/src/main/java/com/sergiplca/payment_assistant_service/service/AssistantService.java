package com.sergiplca.payment_assistant_service.service;

import com.sergiplca.payment_assistant_service.model.dto.AssistantRequestDto;
import com.sergiplca.payment_assistant_service.model.dto.AssistantResponseDto;
import com.sergiplca.payment_assistant_service.model.entity.Embedding;
import com.sergiplca.payment_assistant_service.repository.EmbeddingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.sergiplca.payment_assistant_service.util.EmbeddingUtils.toVectorString;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssistantService {

    private final EmbeddingService embeddingService;
    private final EmbeddingRepository embeddingRepository;
    private final ChatClient chatClient;

    public AssistantResponseDto query(AssistantRequestDto requestDto) {

        log.info("Received question: {}", requestDto.getQuestion());

        var questionEmbedding = embeddingService.embed(requestDto.getQuestion());

        String pgVectorFormat = toVectorString(questionEmbedding.getEmbeddings().get(0));
        List<Embedding> hits = embeddingRepository.findSimilar(pgVectorFormat, 5);
        log.info("Embedded hits: {}", hits.stream().map(Embedding::getContentText));

        String prompt = buildPrompt(requestDto.getQuestion(), hits);
        log.info("Final prompt: {}", prompt);

        return AssistantResponseDto.builder()
            .question(requestDto.getQuestion())
            .response(askOllama(prompt))
            .build();
    }

    public String buildPrompt(String userQuestion, List<Embedding> relevantHits) {

        StringBuilder context = new StringBuilder();

        relevantHits.forEach(hit -> context.append(hit.getContentText()).append("\n\n"));

        return """
        PAYMENT RECORDS:
        %s

        USER QUESTION:
        %s
        """.formatted(context.toString(), userQuestion);
    }

    public String askOllama(String prompt) {

        return chatClient.prompt()
            .user(prompt)
            .call()
            .content();
    }
}
