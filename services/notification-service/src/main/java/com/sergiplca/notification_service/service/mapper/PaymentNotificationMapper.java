package com.sergiplca.notification_service.service.mapper;

import com.sergiplca.notification_service.configuration.MapStructConfig;
import com.sergiplca.notification_service.model.dto.event.PaymentEventDto;
import com.sergiplca.notification_service.model.entity.PaymentNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapStructConfig.class)
public interface PaymentNotificationMapper {

    @Mapping(target = "id", ignore = true)
    PaymentNotification toEntity(PaymentEventDto eventDto);
}
