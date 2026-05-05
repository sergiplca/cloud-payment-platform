# Architecture

## 1. Purpose

This document describes the architecture of the Cloud Payment Platform — a portfolio project that simulates the backend of a fintech payment processing system.

It is a hands-on learning platform designed to explore:

- microservices architecture and service boundaries
- event-driven communication and reliable event publishing
- AI integration via RAG (Retrieval-Augmented Generation)
- observability: metrics, logs, and distributed tracing
- resilience patterns
- cloud-native deployment

Every significant architectural decision made during the evolution of this project is recorded in [docs/adr/](adr/).

---

## 2. Problem statement

The platform allows clients to create orders, process payments, receive notifications, and query their transaction history via a natural language interface.

The purpose is not to replicate a production payment processor in full detail, but to model the core architectural concerns behind this type of system:

- service boundaries and ownership
- synchronous and asynchronous communication
- data consistency and reliable event publishing
- failure handling and graceful degradation
- AI-powered interfaces grounded in real data
- production operability

---

## 3. Services

### API Gateway

The single entry point for all external traffic. Responsible for:

- routing requests to internal services
- enforcing token-based authorization
- exposing the unified Swagger/OpenAPI documentation

Internal services are not accessible directly — all traffic flows through the gateway.

### Order Service

Manages the order lifecycle. Exposes:

- `POST /v1/orders` — create a new order
- `GET /v1/orders/{id}` — retrieve an existing order

Payments are only processed for valid, existing orders. The Payment Service validates the order via a synchronous REST call to this service.

### Payment Service

Processes payment operations. Responsible for:

- creating and persisting payments
- validating that the referenced order exists (synchronous call to Order Service)
- publishing `payment.created` events via the Transactional Outbox Pattern
- enforcing idempotency on `POST /v1/payments` via Redis-cached idempotency keys

The Outbox Pattern ensures that events are never lost even if the Kafka publish fails after a successful DB commit. See [ADR 002](adr/002-outbox-pattern.md).

### Notification Service

An asynchronous event consumer. Responsible for:

- consuming `payment.created` events from Kafka
- persisting a `Notification` record per event
- guaranteeing at-most-once notification creation via event ID deduplication

### Payment Assistant Service

A RAG-powered conversational interface. Responsible for:

- indexing payment and order records as vector embeddings in PostgreSQL (`pgvector`)
- accepting natural language queries via `POST /v1/assistant/query`
- retrieving the most semantically relevant records and passing them as context to the Claude API
- returning a grounded natural language response

The model is constrained via system prompt to only answer from retrieved records and never invent transactions. See [ADR 004](adr/004-rag-payment-assistant.md).

---

## 4. Infrastructure

### PostgreSQL

Primary relational store for all services. Also hosts the `pgvector` extension for the Payment Assistant's embedding store. Each service owns its own schema.

### Kafka

Event bus for asynchronous communication. Current topics:

- `payment.created` — published by Payment Service, consumed by Notification Service and Payment Assistant Service

### Redis

Used by Payment Service for idempotency key storage with TTL-based expiry.

### API (Anthropic)

Used by Payment Assistant Service for:

- generating embeddings from payment and order records
- generating natural language responses grounded in retrieved context

---

## 5. Communication patterns

| Interaction | Pattern | Notes |
|---|---|---|
| Client → services | REST via gateway | All external traffic through API Gateway |
| Payment Service → Order Service | Synchronous REST | Validate order existence before creating payment |
| Payment Service → Kafka | Async / Outbox | Event written to DB first, then published |
| Kafka → Notification Service | Async event-driven | Idempotent consumer |
| Kafka → Payment Assistant | Async event-driven | Indexing pipeline |
| Payment Assistant → Anthropic | Sync REST | Embeddings + completions |

---

## 6. Request flows

### Payment creation flow

```
Client
  → POST /v1/payments (with Idempotency-Key header)
  → API Gateway (auth check, idempotency cache check via Redis)
  → Payment Service
      → GET /v1/orders/{id} → Order Service (validate order exists)
      → Persist payment + outbox event in same DB transaction
      → Outbox poller publishes payment.created to Kafka
  → Kafka
      → Notification Service (persist Notification, deduplicate by event ID)
      → Payment Assistant Service (embed record, store in pgvector)
```

### Assistant query flow

```
Client
  → POST /v1/assistant/query { "question": "..." }
  → API Gateway
  → Payment Assistant Service
      → Embed question via Anthropic embeddings API
      → Cosine similarity search in pgvector → top-k records
      → Build prompt: system constraints + retrieved records + question
      → Claude API → streamed response
  → Client
```

---

## 7. Non-functional concerns

| Concern | Approach |
|---|---|
| Reliability | Transactional Outbox Pattern, idempotency keys, consumer deduplication |
| Resilience | Timeouts, retries with backoff, circuit breaker (Resilience4j), LLM fallback |
| Observability | Prometheus + Grafana (metrics), Zipkin (distributed traces), structured logs |
| Security | Token-based auth at the gateway; internal services not directly accessible |
| Testability | Testcontainers for integration tests against real PostgreSQL and Kafka |
| AI grounding | System prompt constraints, retrieval-only answers, no invented data |

---

## 8. Architecture decisions

All significant decisions are recorded in [docs/adr/](adr/). Key decisions to date:

| ADR | Decision |
|-----|----------|
| [001](adr/001-kafka-over-rest.md) | Kafka for async communication over direct REST calls |
| [002](adr/002-outbox-pattern.md) | Transactional Outbox Pattern for reliable event publishing |
| [003](adr/003-idempotency-keys.md) | Redis-based idempotency keys on the payment endpoint |
| [004](adr/004-rag-payment-assistant.md) | RAG architecture for the Payment Assistant |
| [005](adr/005-observability-strategy.md) | Three-pillar observability: metrics, logs, traces |
| [006](adr/006-resilience-strategy.md) | Circuit breakers, retries, and fallback decisions |

---

## 9. System design diagram

![System design](diagrams/system-design-v1.png)

> This diagram will be updated as the platform evolves. See the [Roadmap](roadmap.md) for current status.

---

## 10. What this architecture deliberately omits

This is a learning and portfolio project. The following are known simplifications relative to a production system:

- **Auth** — the gateway uses a simplified token mechanism. A real system would use OAuth2/OIDC with a proper identity provider.
- **PII** — payment records are embedded and sent to an external API without anonymisation. A production system would require a data handling strategy before any external AI API call.
- **Database-per-service** — services currently share a single PostgreSQL instance. A production system would use separate instances or at minimum strict schema ownership.
- **Kafka** — no schema registry, no multi-partition consumer groups, no compacted topics. These would be introduced incrementally.
- **AI grounding** — the RAG approach reduces hallucination risk but does not eliminate it. A production deployment would require stricter output validation and human review for financial queries.
