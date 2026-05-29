create table paymentassistantservice.embeddings (
    id int8 primary key,
    record_type varchar(50) not null,
    record_id varchar(100) not null,
    content_text text not null,
    embedding vector(768) not null,
    created_at timestamp not null default now(),
    constraint uq_embeddings_record unique (record_type, record_id)
);

-- Vector similarity index (cosine distance)
create index idx_embeddings_vector
    on paymentassistantservice.embeddings using ivfflat (embedding vector_cosine_ops)
    with (lists = 100);

-- Lookup by record type + id (exact lookups / filtering)
create index idx_embeddings_record_type
    on paymentassistantservice.embeddings (record_type, record_id);

-- Partial indexes per data type
create index idx_embeddings_vector_orders
    on paymentassistantservice.embeddings using ivfflat (embedding vector_cosine_ops)
    with (lists = 100)
    where record_type = 'order';

create index idx_embeddings_vector_payments
    on paymentassistantservice.embeddings using ivfflat (embedding vector_cosine_ops)
    with (lists = 100)
    where record_type = 'payment';