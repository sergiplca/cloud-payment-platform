package com.sergiplca.api_gateway.service;

import com.sergiplca.api_gateway.model.dto.user.UserRequestDto;
import com.sergiplca.api_gateway.model.dto.user.UserResponseDto;
import com.sergiplca.api_gateway.model.entity.User;
import com.sergiplca.api_gateway.repository.UserRepository;
import com.sergiplca.api_gateway.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDto createUser(UserRequestDto requestDto) {

        User user = new User();

        user.setUsername(requestDto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(requestDto.getRoles());
        user.setEnabled(true);
        user.setCreationTimestamp(LocalDateTime.now());

        var savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }
}
