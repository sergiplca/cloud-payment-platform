# ADR 001 — Asynchronous communication via Kafka over direct REST calls for payment events

**Status:** Accepted  
**Date:** 2026-05-06

---

## Context

The platform needs a way for `payment-service` to inform other services after a payment has been successfully created. At the time of this decision, the immediate consumer is `notification-service`, which needs to persist a notification record. A future consumer, `payment-assistant-service`, will also need to index the payment for semantic search.

The question is how `payment-service` should communicate the fact that a payment was created to these downstream services.

The system is a portfolio project with a single developer. All services are Java Spring Boot applications running locally via Docker Compose. There is no production traffic and no SLA requirements at this stage.

---

## Decision

Use **Kafka as an event bus** for communication between `payment-service` and its downstream consumers. After a payment is successfully persisted, `payment-service` publishes a `payment.created` event to a Kafka topic. Downstream services consume this event independently and asynchronously.

The primary reasons for this choice over direct REST are:

- **Decoupling.** `payment-service` does not need to know which services care about a payment being created. It publishes one event and its responsibility ends there. Adding a new consumer — such as `payment-assistant-service` — requires no change to the producer.
- **Temporal independence.** If `notification-service` is down when a payment is created, the event remains in the Kafka topic and will be processed when the service recovers. With a direct REST call, a downstream outage would either fail the payment request or require complex retry logic inside the producer.
- **Natural audit trail.** Kafka topics act as an append-only log of domain events. This is useful for debugging and is a foundation for event sourcing patterns if needed in the future.

---

## Alternatives considered

### Direct REST call from `payment-service` to `notification-service`

`payment-service` calls `POST /v1/notifications` on `notification-service` synchronously as part of the payment creation flow.

**Rejected because:**

- It introduces temporal coupling. If `notification-service` is slow or unavailable, the payment creation either fails or hangs, which is not acceptable. A notification is a side effect of a payment — it should never be able to block or fail a payment.
- It requires `payment-service` to know the address and API contract of `notification-service`. Adding a second consumer (such as `payment-assistant-service`) would require modifying `payment-service`.
- It does not scale to multiple consumers cleanly.

### Synchronous REST with fire-and-forget (async thread)

`payment-service` calls downstream services in a background thread, not blocking the payment response.

**Rejected because:**

- This still requires `payment-service` to know about its consumers.
- If the background thread fails after the payment is committed, the notification is silently lost. There is no durability guarantee.
- It is complexity without the benefits of a proper event bus.

### Serialization format: Avro or Protobuf instead of JSON

Binary serialization formats such as Avro (common in the Confluent/Kafka ecosystem) or Protobuf would enforce schema compatibility between producers and consumers automatically. With a Schema Registry, a breaking schema change — removing a required field, renaming a field, changing a type — would be rejected at deployment time rather than discovered as a silent bug in production.

**Not adopted at this stage because:**

- The coordination problem these tools solve arises when multiple independent teams produce and consume the same topics. On a single-developer project, schema coordination is handled through `docs/events.md` and the fact that producer and consumer are written by the same person.
- Introducing Avro and a Schema Registry would add meaningful infrastructure complexity and a learning curve disproportionate to the problem being solved.

**What would change at scale:** If the platform grew to multiple independent teams consuming `payment.created`, Confluent Schema Registry with Avro would be the natural next step. It would enforce backward and forward compatibility rules automatically, turning "team forgot to update the consumer" from a silent production incident into a build failure.

---

## Consequences

### Positive

- `payment-service` is fully decoupled from its consumers. It publishes one event and has no dependency on what happens next.
- New consumers can be added without modifying the producer.
- Downstream failures do not affect payment creation.
- The Kafka topic provides a durable, replayable record of all payment events, useful for debugging and potential future use cases.
- **Eventual consistency.** We reduce the costs of implementing a high-availability system as a notification delay would pose no issue to the end user.

### Negative

- **At-least-once delivery.** Kafka does not guarantee exactly-once delivery in all configurations. Consumers must implement deduplication logic using the `eventId` field. This is addressed in `notification-service` via event ID storage and a unique constraint. See also [ADR 003](003-idempotency-keys.md).
- **Eventual consistency.** There is no guarantee that `notification-service` has processed the event by the time the payment creation response reaches the client. This is acceptable for notifications but would not be acceptable for operations that require synchronous confirmation.
- **Operational complexity.** Running Kafka locally requires Kafka and Zookeeper in Docker Compose. This is more infrastructure than a direct REST call. Debugging a broken async flow is harder than debugging a failed HTTP call.
- **No schema enforcement.** Using JSON without a schema registry means that a breaking change to the event payload will not be caught automatically. This is mitigated by `docs/events.md` as the explicit contract, but the enforcement is manual.

---

## References

- [Event catalog — payments.payment.created](../events.md)
- [ADR 002 — Transactional Outbox Pattern](002-outbox-pattern.md)
- [ADR 003 — Idempotency keys on the payment endpoint](003-idempotency-keys.md)