package com.sergiplca.api_gateway.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class TokenRequestDto {

    private String username;

    private List<String> roles;
}
