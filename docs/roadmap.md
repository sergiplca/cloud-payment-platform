# Roadmap

## Purpose

This roadmap describes how the project will evolve from a local service skeleton into a more complete cloud-native backend platform.

The goal is not to build everything at once, but to grow the system in controlled iterations while learning the architectural trade-offs behind each step.

---

## Current status

The project currently includes:

- PostgreSQL running locally through Docker Compose
- four Spring Boot services:
    - `api-gateway`
    - `payment-service`
    - `order-service`
    - `notification-service`
- local service connectivity to PostgreSQL
- initial architecture documentation
- local setup documentation
- an initial system design diagram

At this stage:

- the service skeletons are in place
- infrastructure for local development exists
- simple business logic in payment-service and order-service
- api-gateway routes requests to appropriate services
- api-gateway serves a fake token and endpoints require it to work
- business logic services hidden behind the gateway

---

## Project objective

The long-term objective is to build a portfolio project that demonstrates practical understanding of:

- backend architecture
- microservices
- distributed systems
- event-driven communication
- observability
- resilience
- Kubernetes
- cloud deployment
- infrastructure as code

The project is also intended to document architectural decisions and implementation trade-offs in a way that is useful both for learning and for portfolio presentation.

---

## Development phases

### Phase 1 — Foundation
**Status:** Done

**Goal:** establish the project baseline

Included in this phase:
- define project structure
- configure local environment
- run PostgreSQL
- run service skeletons
- validate service-to-database connectivity
- create initial documentation and diagrams

**Expected outcome:**
A reproducible local development baseline with clear documentation.

---

### Phase 2 — First business flow
**Status:** Done

**Goal:** implement the first real capability in `payment-service`

Planned work:
- create a `POST /payments` endpoint
- validate request payload
- persist payment data in PostgreSQL
- return a minimal response with identifier and status
- document the first end-to-end flow

**Expected outcome:**
A working backend service that performs a real operation and persists data.

---

### Phase 3 — Service boundaries and synchronous communication
**Status:** Done

**Goal:** introduce a clearer role for `order-service`

Planned work:
- define `order-service` responsibility
- add a minimal order flow
- optionally connect `payment-service` and `order-service` through REST
- refine domain boundaries

**Expected outcome:**
A better understanding of service ownership and synchronous inter-service communication.

---

### Phase 4 — API Gateway routing
**Status:** Done

**Goal:** Hide business-logic services behind an API Gateway

Planned work:
- create api-gateway service
- add routes to the rest of services
- hide the services so they are not accessible directly
- document API changes
- Implement security

**Expected outcome:**
Application is accessible through the Gateway instead of directly to each service and authorization is required.

---

### Phase 5 — Event-driven communication
**Status:** In Progress

**Goal:** move from purely synchronous communication to asynchronous workflows

Planned work:
- add Kafka and Zookeeper locally
- publish payment-related events
- consume events in `notification-service`
- document event contracts and message flow

**Expected outcome:**
First end-to-end asynchronous flow between services.

---

### Phase 6 — Kubernetes deployment
**Status:** Planned

**Goal:** deploy services in Kubernetes without moving every dependency there immediately

Planned work:
- deploy `payment-service` in Minikube or Kind
- add Kubernetes manifests
- use ConfigMaps and Secrets
- validate service connectivity
- later deploy additional services

**Expected outcome:**
Basic understanding of service deployment and orchestration in Kubernetes.

---

### Phase 7 — Observability
**Status:** Planned

**Goal:** observe system behavior once services do something meaningful

Planned work:
- expose metrics through Spring Boot Actuator
- add Prometheus
- add Grafana
- create basic dashboards
- observe request count, latency and errors

**Expected outcome:**
Ability to inspect how the system behaves under normal and failing conditions.

---

### Phase 8 — Resilience
**Status:** Planned

**Goal:** design for failure instead of assuming perfect communication

Planned work:
- add retries
- add timeouts
- add circuit breakers
- simulate dependency failures
- document the trade-offs

**Expected outcome:**
A more production-oriented backend architecture.

---

### Phase 9 — Infrastructure as Code and cloud proof of concept
**Status:** Planned

**Goal:** provision and deploy a reduced but meaningful part of the system in AWS

Planned work:
- create Terraform structure
- define minimal AWS infrastructure
- deploy a limited PoC
- document setup and decisions
- keep cloud scope realistic and low-cost

**Expected outcome:**
Infrastructure reproducibility and a first cloud deployment story for the project.

---

## Guiding principles

This project follows a deliberate order:

1. First, make it work locally
2. Then, make it understandable
3. Then, make it observable
4. Then, make it resilient
5. Finally, make it cloud-ready

Other principles:

- do not introduce unnecessary infrastructure complexity too early
- document architectural decisions as the project evolves
- keep the repo understandable for external readers
- prefer incremental progress over premature completeness