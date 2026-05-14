# ADR 002 — Making use of Outbox Pattern to publish events to Kafka

**Date:** 2026-05-14

---

## Context

Following up on [ADR001 - Kafka over REST](001-kafka-over-rest.md), publishing messages to Kafka so consumers can obtain them needs more consistency, reliability, failure handling, and operational resilience. At this point the payment process as a whole depends on its publishing, which means two possible scenarios depending on its implementation:

- **Inconsistency.** If storing the payment on the DB and publishing it over Kafka is done in two separate transactions and Kafka is to fail, the payment would succeed but consumers would not know about it. As of now `notification-service` is the only consumer so no notifications would be received, but if there was a hypothetic reconciliation process, the payment would not be part of it.  
On the other hand, the DB transaction failing would mean payments being reconciliated without even existing as the payment process failed, or the client would be notified of a successful payment that never happened.

- **High dependency.** If both persistence and publishing were part of the same transaction, Kafka would be on the critical path. Any downtime on Kafka or any publishing failure would mean a failed payment, increasing the whole failure ratio overall.
---

## Decision

Use **Outbox Pattern** to store the event in an outbox table instead of publishing it directly to Kafka. After a payment is successfully persisted, `payment-service` stores the event in a DB table which will be polled and processed by a separate scheduled task.

The primary reasons for this choice over direct publishing are:

- **Preventing dual-write inconsistency.** Writing the payment both on the payment and outbox tables provides atomicity. A retried API call because of a failure in Kafka would mean two persisted payments and inconsistency on the downstream.
- **Keeping the critical path as simple as possible.** Any Kafka downtime does not affect the payment API, so the incident criticality is considerably reduced and events can be sent downstream later.
- **Failure recovery.** Unpublished events can be safely replayed, and consistently failing ones can be inspected.
- **Observability.** Visibility is given to pending events, failed deliveries, retry counts and publishing latency.
- **Idempotency**: Publisher can safely retry, and consumers can avoid duplication using the event ID.

---

## Alternatives considered

### Change Data Capture (CDC) directly from the database

`payment-service` only takes care of writing to the database, then a CDC tooling monitors the table and publishes to Kafka.

**Rejected because:**

- It introduces infrastructure complexity because of having to put a CDC platform in place.
- Database schema changes need to be made visible externally, directly impacting schema isolation.

---

## Tradeoffs

- **Increased complexity.** A new database table and a polling process is needed, and both need to provide a cleanup and retry strategies.
- **Eventual consistency.** There is a delay between the payment persistence and its downstream publish. This is preferred to fragile immediate consistency.
- **Additional Storage** Outbox data must be stored, cleaned up and audited periodically.

---

## References

- [ADR001 - Kafka over REST](001-kafka-over-rest.md)