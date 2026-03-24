package com.sergiplca.order_service.service.mapper;

import com.sergiplca.order_service.configuration.MapStructConfig;
import com.sergiplca.order_service.model.dto.OrderRequestDto;
import com.sergiplca.order_service.model.dto.OrderResponseDto;
import com.sergiplca.order_service.model.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "creationTimestamp", ignore = true)
    Order toEntity(OrderRequestDto request);

    @Mapping(target = "orderId", source = "id")
    OrderResponseDto toResponse(Order payment);
}
