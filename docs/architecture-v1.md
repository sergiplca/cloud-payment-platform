# Architecture v1

## 1. Purpose

This project aims to simulate a cloud-native payment platform in order to practice backend architecture and distributed systems concepts.

It is intended as a hands-on learning project focused on:
- microservices
- event-driven systems
- scalability
- resilience
- observability
- cloud-native deployment patterns

## 2. Problem statement

The platform should allow clients to create payment requests and process them through a simple distributed architecture.

The purpose is not to replicate a real payment processor in full detail, but to model the core architectural concerns behind this type of system:
- service boundaries
- asynchronous communication
- data consistency
- failure handling
- scalability
- production operability

## 3. Initial scope

The first version of the project will include the following components:

### API Gateway
Responsible for receiving external requests and routing them to internal services, as well as managing authorization.

### Order Service
Responsible for creating and managing payment orders.

### Payment Service
Responsible for processing payment-related operations.

### Notification Service
Responsible for sending status updates after payment events.

### PostgreSQL
Primary relational database for transactional data.

### Kafka
Event bus for asynchronous communication between services.

### Redis
Used later for caching, rate limiting or temporary state if needed.

## 4. Initial architecture decisions

### Why microservices?
The goal of this project is to understand service boundaries, distributed communication and operational complexity in backend systems.

### Why Kafka?
Kafka will help model asynchronous communication and event-driven workflows.

### Why PostgreSQL?
PostgreSQL provides a solid relational model for transactional data and is widely used in backend systems.

### Why Redis?
Redis is useful for patterns such as caching, rate limiting and short-lived state management.

## 5. Initial flow

The initial flow will be:

1. A client sends a payment request
2. The API Gateway routes the request
3. The Order Service creates the order
4. The Payment Service processes the payment
5. A payment-related event is published
6. The Notification Service reacts to the event

This flow will evolve as the project grows.

## 6. Non-functional concerns to explore

The project is also intended to explore non-functional requirements such as:

- security
- scalability
- reliability
- observability
- resilience
- deployment automation
- service communication patterns

## 7. Future iterations

Later iterations may include:
- fraud detection service
- authentication / authorization
- CI/CD pipelines
- infrastructure as code
- metrics and tracing
- retries and circuit breakers
- Kubernetes deployment
- cloud deployment on AWS

## 8. System design v1

![System design v1](diagrams/system-design-v1.png)