# Roadmap

## Purpose

This roadmap describes how the project evolves from a local service skeleton into a complete cloud-native backend platform.

The goal is not to build everything at once, but to grow the system in controlled iterations while documenting the architectural trade-offs behind each step.

---

## Current status

**Active phase:** Phase 6 — Event robustness and idempotency

The platform currently includes:

- five Spring Boot services: `api-gateway`, `order-service`, `payment-service`, `notification-service`
- full business logic in `order-service` and `payment-service`
- API Gateway routing with token-based auth
- PostgreSQL running via Docker Compose, connected to all services
- Kafka and Zookeeper running locally via Docker Compose
- initial architecture documentation and system design diagram
- initial ADR structure under `docs/adr/`

---

## Project objective

The long-term objective is to demonstrate practical understanding of:

- microservices architecture and service boundaries
- distributed systems trade-offs
- event-driven communication and reliable event publishing
- AI integration (RAG, vector search, LLM APIs)
- observability: metrics, logs, and distributed tracing
- resilience patterns
- Kubernetes deployment
- cloud deployment and infrastructure as code

The project also documents every significant architectural decision in [Architecture Decision Records](adr/) to demonstrate not just what was built, but why.

---

## Development phases

### Phase 1 — Foundation
**Status:** Done

Establish the project baseline: project structure, local environment, PostgreSQL, service skeletons, service-to-database connectivity, initial documentation and diagrams.

---

### Phase 2 — First business flow
**Status:** Done

Implement the first real capability in `payment-service`: `POST /v1/payments` endpoint, payload validation, persistence, and minimal response.

---

### Phase 3 — Service boundaries and synchronous communication
**Status:** Done

Define `order-service` responsibility, implement the order flow, connect `payment-service` and `order-service` via REST, and validate the payment only if the order exists.

---

### Phase 4 — API Gateway
**Status:** Done

Route all external traffic through the gateway, hide internal services from direct access, and enforce token-based authorization at the edge.

---

### Phase 5 — Event-driven communication
**Status:** Done

Payments to be published to `payments.payment.created` topic in Kafka and `notification-service` to consume and persist them.  
Define and document the event schema in [Events](events.md).  
Document [ADR-001 - Kafka over REST](adr/001-kafka-over-rest.md).

---

### Phase 6 — Event robustness and idempotency
**Status:** In Progress

**Goal:** make the event layer production-realistic and introduce fintech-standard reliability patterns

Planned work:
- implement the Transactional Outbox Pattern in `payment-service`: write the event to an `outbox` table in the same DB transaction as the payment, then publish from there
- add consumer-side duplicate detection in `notification-service` using event ID and a DB unique constraint
- add idempotency keys to `POST /v1/payments` via Redis: clients send an `Idempotency-Key` header and the response is cached for 24 hours
- add consumer error handling and a dead-letter topic for unprocessable messages
- write ADR 002 (Outbox Pattern) and ADR 003 (idempotency keys)

**Expected outcome:**
A payment flow that is safe to retry, resilient to duplicate events, and honest about the failure modes of direct event publishing.

---

### Phase 7 — Payment Assistant (AI / RAG)
**Status:** Planned

**Goal:** add a conversational AI feature that models what a real fintech would build for its clients

Planned work:
- create `payment-assistant-service` as a new Spring Boot service registered in the gateway
- enable the `pgvector` extension on the existing PostgreSQL instance
- build an indexing pipeline: consume `payment.created` events from Kafka, embed the payment record via the Anthropic embeddings API, and store the vector in pgvector
- build a query endpoint `POST /v1/assistant/query` that embeds the user question, runs a cosine similarity search, retrieves the top relevant records, and passes them to the Claude API as RAG context
- constrain the system prompt so the model only answers from retrieved records and never invents transactions
- write ADR 004: RAG architecture, why not fine-tuning, hallucination risk, production considerations (PII, audit logging, rate limiting)

**Expected outcome:**
A live AI feature backed by real platform data, with an honest architectural ADR documenting the design decisions and their limits.

---

### Phase 8 — Testing
**Status:** Planned

**Goal:** establish engineering discipline with production-realistic tests

Planned work:
- unit tests for `payment-service` (service layer, validation, status transitions) and `order-service`
- integration tests for `payment-service` using Testcontainers with a real PostgreSQL instance, including Outbox table writes
- integration test for the Kafka consumer in `notification-service` using Testcontainers Kafka
- define a `docs/definition-of-done.md` covering the minimum quality bar for any merged endpoint

**Expected outcome:**
Tests that validate real behaviour against real infrastructure, not mocked substitutes.

---

### Phase 9 — Observability
**Status:** Planned

**Goal:** instrument the full system with metrics, logs, and distributed traces

Planned work:
- Spring Boot Actuator across all services
- Prometheus scraping and a Grafana dashboard: request count, latency (p50/p99), error rate, JVM heap, LLM call duration
- distributed tracing with Micrometer + Zipkin, propagating trace IDs across all services including `payment-assistant-service`
- write ADR 005: three-pillar observability strategy and what would be added in production (alerting, SLOs, LLM token cost tracking)

**Expected outcome:**
The ability to follow a single request as a unified trace across all five services, and to observe system behaviour in Grafana.

---

### Phase 10 — Resilience
**Status:** Planned

**Goal:** design for failure rather than assuming perfect communication

Planned work:
- timeouts and retries with exponential backoff on the `payment-service` → `order-service` REST call
- circuit breaker with Resilience4j on the same call
- timeout and graceful fallback on the Anthropic API call in `payment-assistant-service`
- simulate failures and observe circuit breaker state transitions in Grafana
- write ADR 006: resilience strategy, retry storm prevention, fallback decisions

**Expected outcome:**
A system that degrades gracefully rather than cascading on downstream failures.

---

### Phase 11 — CI/CD
**Status:** Planned

**Goal:** automate build, test, and packaging with a production-oriented pipeline

Planned work:
- GitHub Actions workflow: build all services, run unit tests on every push to main
- integration test stage using Testcontainers as the primary quality gate
- build and push Docker images to GitHub Container Registry, tagged with commit SHA
- PR template checklist aligned with the Definition of Done
- document the pipeline in `docs/cicd.md`

**Expected outcome:**
Every merge to main is validated automatically and produces a tagged Docker image.

---

### Phase 12 — Kubernetes and infrastructure as code
**Status:** Planned

**Goal:** deploy the platform in Kubernetes and provide a minimal but honest cloud story

Planned work:
- Deployment, Service, ConfigMap, and Secret manifests for `payment-service` and `api-gateway` in Minikube or Kind
- liveness and readiness probes via Spring Boot Actuator health endpoints
- `docs/setup-kubernetes.md`
- Terraform PoC: minimal AWS infrastructure (VPC, security group) for the gateway and one service only; `docs/setup-aws.md` with honest scope, cost, and limitations

**Expected outcome:**
A reproducible Kubernetes deployment and a minimal cloud story that is honest about what runs on AWS and what remains local.

---

## Guiding principles

This project follows a deliberate order:

1. First, make it work locally
2. Then, make it reliable (event robustness, idempotency)
3. Then, make it more capable (AI integration)
4. Then, make it trustworthy (testing)
5. Then, make it observable
6. Then, make it resilient
7. Finally, make it cloud-ready

Additional principles:

- document architectural decisions as the project evolves — every significant decision gets an ADR
- do not introduce infrastructure complexity before it earns its place
- keep the repo understandable to an external reader at all times
- prefer incremental progress over premature completeness
