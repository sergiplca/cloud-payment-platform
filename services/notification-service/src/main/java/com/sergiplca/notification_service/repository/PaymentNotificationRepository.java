package com.sergiplca.notification_service.repository;

import com.sergiplca.notification_service.model.entity.PaymentNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentNotificationRepository extends JpaRepository<PaymentNotification, Long> {
}
