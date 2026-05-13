package com.sergiplca.payment_service.service.mapper;

public interface EventMapper<E> {

    <T> E createEvent(String eventType, T payload);
}
