package com.sergiplca.payment_service.service.mapper;

import com.sergiplca.payment_service.configuration.MapStructConfig;
import com.sergiplca.payment_service.model.dto.PaymentRequestDto;
import com.sergiplca.payment_service.model.dto.PaymentResponseDto;
import com.sergiplca.payment_service.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "creationTimestamp", ignore = true)
    Payment toEntity(PaymentRequestDto request);

    @Mapping(target = "paymentId", source = "id")
    PaymentResponseDto toResponse(Payment payment);
}
