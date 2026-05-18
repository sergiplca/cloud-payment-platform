-- flyway:executeInTransaction=false

create unique index payment_notification_payment_id ON notificationservice.payment_notification (payment_id);