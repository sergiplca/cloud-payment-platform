create sequence if not exists paymentassistantservice.embedding_sequence start with 1 increment by 1;

create table paymentassistantservice.embedding (
    id int8 not null default nextval('paymentassistantservice.embedding_sequence'),
    record_type varchar(50) not null,
    record_id varchar(100) not null,
    content_text text not null,
    embedding vector(768) not null,
    created_at timestamp not null default now(),
    primary key (id),
    constraint uq_embeddings_record unique (record_type, record_id)
);

-- Vector similarity index (cosine distance)
create index idx_embeddings_vector
    on paymentassistantservice.embedding using ivfflat (embedding vector_cosine_ops)
    with (lists = 100);

-- Lookup by record type + id (exact lookups / filtering)
create index idx_embeddings_record_type
    on paymentassistantservice.embedding (record_type, record_id);

-- Partial indexes per data type
create index idx_embeddings_vector_orders
    on paymentassistantservice.embedding using ivfflat (embedding vector_cosine_ops)
    with (lists = 100)
    where record_type = 'order';

create index idx_embeddings_vector_payments
    on paymentassistantservice.embedding using ivfflat (embedding vector_cosine_ops)
    with (lists = 100)
    where record_type = 'payment';