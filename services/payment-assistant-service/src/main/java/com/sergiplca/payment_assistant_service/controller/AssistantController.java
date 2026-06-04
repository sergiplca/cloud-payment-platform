package com.sergiplca.payment_assistant_service.controller;

import com.sergiplca.payment_assistant_service.model.dto.AssistantRequestDto;
import com.sergiplca.payment_assistant_service.model.dto.AssistantResponseDto;
import com.sergiplca.payment_assistant_service.service.AssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/assistant")
@RequiredArgsConstructor
public class AssistantController implements AssistantControllerSwaggerSpec {

    private final AssistantService assistantService;

    @Override
    @PostMapping("/query")
    @ResponseStatus(HttpStatus.OK)
    public AssistantResponseDto query(@RequestBody AssistantRequestDto requestDto) {

        return assistantService.query(requestDto);
    }
}
