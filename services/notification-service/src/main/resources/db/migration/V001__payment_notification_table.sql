create schema if not exists notificationservice;

create sequence if not exists notificationservice.payment_notification_sequence start with 1 increment by 1;

create table notificationservice.payment_notification (
    id int8 not null default nextval('notificationservice.payment_notification_sequence'),
    payment_id int8 not null,
    amount numeric(10,2) not null,
    currency varchar(3) not null,
    customer_reference varchar not null,
    status varchar not null,
    creation_timestamp timestamp not null,
    order_id int8 not null,
    primary key (id)
);