# Event Catalog

This document is the source of truth for all events published on the platform's Kafka broker.

Every event produced by any service must be documented here before or alongside its implementation.

---

## Conventions

### Naming

Topics follow the pattern `{owner}.{domain}.{event}` in lowercase with dots as separators:

```
payments.payment.created
payments.payment.updated
orders.order.cancelled
```

### Envelope

Every event is a JSON object with two top-level sections: a metadata **envelope** and a **payload** containing the domain data. The envelope fields are the same across all events.

```json
{
  "eventId":        "<uuid>",
  "eventType":      "<domain-name>",
  "eventTimestamp": "<ISO-8601 UTC timestamp>",
  "payload":        { }
}
```

| Field            | Type   | Description                                                                     |
|------------------|--------|---------------------------------------------------------------------------------|
| `eventId`        | UUID   | Unique identifier for this event instance. Used by consumers for deduplication. |
| `eventType`      | String | Mirrors the Kafka topic domain name.                                            |
| `eventTimestamp` | String | ISO-8601 timestamp in UTC at which the event occurred in the domain.            |
| `payload`        | Object | Domain-specific data. Structure varies per event type.                          |

### Serialization

All events are serialized as **UTF-8 JSON**. No Avro or Protobuf schema registry is used at this stage. This is a known simplification — see [ADR 001](adr/001-kafka-over-rest.md) for the reasoning.

### Idempotency

Consumers must treat events as **at-least-once delivered**. The `eventId` field must be used to detect and discard duplicates. Consumers are responsible for their own deduplication logic.

---

## Events

---

### `payments.payment.created`

**Topic:** `payments.payment.created`  
**Producer:** `payment-service`  
**Status:** Active  
**Version:** `1.0`

Published after a payment has been successfully validated and persisted in the database. The event is written to the `outbox` table in the same database transaction as the payment record, and published to Kafka by the outbox poller. This guarantees the event is never lost even if the broker is temporarily unavailable at the moment of payment creation.

See [ADR 002](adr/002-outbox-pattern.md) for the rationale behind the Outbox Pattern.

#### Consumers

| Service                     | Purpose                                                  |
|-----------------------------|----------------------------------------------------------|
| `notification-service`      | Persists a `Notification` record. Deduplicates by `eventId`. |

#### Schema

**Envelope**

| Field            | Type   | Example                                |
|------------------|--------|----------------------------------------|
| `eventId`        | UUID   | `"a3f1c2d4-..."`                       |
| `eventType`      | String | `"payment.created"`                    |
| `eventTimestamp` | String | `"2026-05-06T10:30:00Z"`              |

**Payload**

| Field               | Type              | Nullable | Description                                              |
|---------------------|-------------------|----------|----------------------------------------------------------|
| `paymentId`         | UUID              | No       | Unique identifier of the payment.                        |
| `orderId`           | UUID              | No       | Identifier of the associated order.                      |
| `amount`            | Decimal           | No       | Payment amount. Two decimal places. Always positive.     |
| `currency`          | String (ISO 4217) | No    | Three-letter currency code. Example: `EUR`, `USD`.       |
| `status`            | String (enum)     | No       | Initial status of the payment. Always `CREATED` in this event. |
| `creationTimestamp` | Instant           | No       | ISO-8601 UTC timestamp at which the payment was persisted. |

**Payload enum values**

`status`: `CREATED`

#### Example

```json
{
  "eventId": "a3f1c2d4-8b3e-4f7a-9c1d-2e5b6f0a4d8c",
  "eventType": "PAYMENT",
  "eventTimestamp": "2026-05-06T09:18:00.975993433",
  "payload": {
    "paymentId": "5462",
    "orderId": "42",
    "amount": 149.99,
    "currency": "EUR",
    "customerReference": "abc",
    "status": "CREATED",
    "creationTimestamp": "2026-05-06T10:29:59Z"
  }
}
```

#### Failure handling

If `notification-service` cannot process this event (deserialization error, downstream failure), the message is routed to the dead-letter topic `payment.created.DLT`. The event is logged with enough context to support manual reprocessing. No automatic retry beyond the consumer's configured retry policy.

---

## Changelog

| Date       | Event                      | Version | Change                          |
|------------|----------------------------|---------|---------------------------------|
| 2026-05-06 | `payments.payment.created` | 1.0     | Initial definition              |