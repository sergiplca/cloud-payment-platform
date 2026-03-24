create schema if not exists orderservice;

create sequence if not exists orderservice.order_sequence start with 1 increment by 1;

create table orderservice.order (
    id int8 not null default nextval('orderservice.order_sequence'),
    amount numeric(10,2) not null,
    currency varchar(3) not null,
    customer_reference varchar not null,
    status varchar not null,
    creation_timestamp timestamp not null,
    primary key (id)
);