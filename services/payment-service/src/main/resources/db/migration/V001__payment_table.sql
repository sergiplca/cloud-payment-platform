create schema if not exists paymentservice;

create sequence if not exists paymentservice.payment_sequence start with 1 increment by 1;

create table paymentservice.payment (
    id int8 not null default nextval('paymentservice.payment_sequence'),
    amount numeric(10,2) not null,
    currency varchar(3) not null,
    customer_reference varchar not null,
    status varchar not null,
    creation_timestamp timestamp not null,
    primary key (id)
);