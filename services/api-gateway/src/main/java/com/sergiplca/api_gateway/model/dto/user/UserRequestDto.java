package com.sergiplca.api_gateway.model.dto.user;

import lombok.Data;

import java.util.List;

@Data
public class UserRequestDto {

    private String username;

    private String password;

    List<String> roles;
}
