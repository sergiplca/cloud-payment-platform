create sequence if not exists paymentservice.outbox_sequence start with 1 increment by 1;

create table paymentservice.outbox_event (
    id int8 not null default nextval('paymentservice.outbox_sequence'),
    payload varchar not null,
    event_type varchar not null,
    destination_topic varchar not null,
    creation_timestamp timestamp not null,
    primary key (id)
);