# Cloud Payment Platform

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Cloud Payment Platform is a portfolio project that simulates the backend of a fintech payment processing system. It is designed to explore and demonstrate practical understanding of microservices architecture, distributed systems, event-driven communication, AI integration, observability, resilience, and cloud-native deployment.

The goal of this project is not only to build a working system, but to document the reasoning behind each architectural decision and use the platform as a hands-on learning environment.

---

## Architecture overview

The platform is composed of five Spring Boot services communicating synchronously via REST and asynchronously via Kafka:

```
Client
  └── API Gateway (auth, routing)
        ├── Order Service       (order lifecycle)
        ├── Payment Service     (payment processing, idempotency, Outbox)
        ├── Notification Service (async event consumer)
        └── Payment Assistant   (RAG-powered conversational interface)
```

- **PostgreSQL** — primary relational store for all services, with `pgvector` extension for semantic embeddings in the Payment Assistant
- **Kafka** — event bus for async communication between Payment Service, Notification Service, and Payment Assistant
- **Redis** — idempotency key store for the Payment Service

See [Architecture](docs/architecture.md) for the full design and [docs/adr/](docs/adr/) for the decisions behind it.

---

## Stack

| Category        | Technology                          |
|-----------------|-------------------------------------|
| Language        | Java 17 (LTS)                       |
| Framework       | Spring Boot 4.0.4                   |
| Database        | PostgreSQL 14 + pgvector extension  |
| ORM             | Spring Data JPA / Hibernate         |
| Build           | Gradle                              |
| API docs        | springdoc-openapi                   |
| Message broker  | Kafka                               |
| Caching         | Redis                               |
| AI              | Anthropic Claude API                |
| Observability   | Prometheus / Grafana / Zipkin       |
| Testing         | JUnit 5 / Testcontainers            |
| CI/CD           | GitHub Actions                      |
| Container       | Docker / Kubernetes                 |
| IaC             | Terraform (AWS PoC)                 |

---

## Quickstart

For setup instructions, see:

- [Local (Docker) setup](docs/setup-local.md)
- [Kubernetes setup](docs/setup-kubernetes.md) — Planned
- [AWS setup](docs/setup-aws.md) — Planned

---

## API documentation

Once the applications are running, the full interactive API documentation is available through the gateway:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI spec:** `http://localhost:8080/v3/api-docs`

### Endpoints

| Method | Path                       | Service                   | Description                          |
|--------|----------------------------|---------------------------|--------------------------------------|
| `POST` | `/auth/token`              | api-gateway               | Obtain a bearer token                |
| `POST` | `/v1/orders`               | order-service             | Create a new order                   |
| `GET`  | `/v1/orders/{id}`          | order-service             | Retrieve an existing order           |
| `POST` | `/v1/payments`             | payment-service           | Create a payment (idempotency-keyed) |
| `POST` | `/v1/assistant/query`      | payment-assistant-service | Natural language query over payments |

---

## Architecture Decision Records

Every significant architectural decision in this project is documented in [docs/adr/](docs/adr/). ADRs record what was decided, why, what alternatives were considered, and what the trade-offs are.

| ADR | Decision |
|-----|----------|
| [001](docs/adr/001-kafka-over-rest.md) | Kafka for async communication over direct REST calls |
| [002](docs/adr/002-outbox-pattern.md) | Transactional Outbox Pattern for reliable event publishing |
| [003](docs/adr/003-idempotency-keys.md) | Redis-based idempotency keys on the payment endpoint |
| [004](docs/adr/004-rag-payment-assistant.md) | RAG architecture for the Payment Assistant service |
| [005](docs/adr/005-observability-strategy.md) | Three-pillar observability: metrics, logs, and traces |
| [006](docs/adr/006-resilience-strategy.md) | Circuit breakers, retries, and fallback decisions |

---

## Roadmap

See [docs/roadmap.md](docs/roadmap.md) for the full development roadmap and current status.
