package com.sergiplca.api_gateway.controller;

import com.sergiplca.api_gateway.model.dto.user.UserRequestDto;
import com.sergiplca.api_gateway.model.dto.user.UserResponseDto;
import com.sergiplca.api_gateway.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController implements UserControllerSwaggerSpec {

    private final UserService userService;

    @Override
    @PostMapping("/create")
    public UserResponseDto createToken(@RequestBody UserRequestDto userRequestDto) {

        return userService.createUser(userRequestDto);
    }
}
