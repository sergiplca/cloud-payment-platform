package com.sergiplca.api_gateway.service.mapper;

import com.sergiplca.api_gateway.configuration.MapStructConfig;
import com.sergiplca.api_gateway.model.dto.user.UserResponseDto;
import com.sergiplca.api_gateway.model.entity.User;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    UserResponseDto toResponse(User user);
}
