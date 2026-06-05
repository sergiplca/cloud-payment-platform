create sequence if not exists apigateway.user_sequence start with 1 increment by 1;

create table apigateway.user (
    id int8 not null default nextval('apigateway.user_sequence'),
    username varchar not null,
    password_hash text not null,
    roles varchar(50)[] not null,
    enabled boolean not null,
    creation_timestamp timestamp not null,
    primary key (id)
);