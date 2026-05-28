# ADR 003 — Using cached idempotency keys as a safeguard for duplicated payments

**Date:** 2026-05-28

---

## Context

`POST /v1/payments` is a non-idempotent that charges a customer via a payment. Networks are unreliable, so a client can send a payment request to the API which can be successfully processed and committed, but the response can be lost somewhere between the server and the client. As a result, the client sees a timeout and retries, resulting in:

- The customer being charged twice.
- Duplicate payments in the database.
- Manual interventions in the reconciliation process and increased customer support burden.

Many payment processors address this problem using **idempotency keys**, a client supplied token to deduplicate requests within a time frame.

---

## Decision

Use **Idempotency keys** for `POST /v1/payments` using **Redis** as a cache provider with a 24 hour TTL.

- Clients supply an Idempotency-Key header on every payment creation request.
- The payment service checks Redis before processing.
  - Cache hit → return the stored response immediately (no double processing and charge).
  - Cache miss → process the payment, then store the full response in Redis before returning.
- An in-flight lock (Redis SET NX) prevents race conditions when concurrent retries arrive before the first request finishes.

### Why Redis?

| Option | Pros | Cons                                                                   |
|---|---|------------------------------------------------------------------------|
| **Redis** | Sub-millisecond reads, built-in TTL, widely understood | Additional infrastructure dependency                                   |
| **Database table** | No new infra, durable | Adds latency to every payment request; requires cleanup job for expiry |
| **In-memory (single node)** | Zero infra | Lost on restart; not possible under horizontal scaling                 |

Redis is the canonical choice for short-lived, high-read auxiliary data.

### Why a client-generated key?

The server cannot generate the key itself — it has no way to identify a "retry" from a "new request" without external context. The client is the only party that knows it is retrying, so the client must supply the key. A **UUID v4** provides sufficient entropy to make accidental collisions negligible.

### Why a 24-hour TTL?

- Long enough to cover retries from any reasonable timeout or backoff strategy.
- Short enough to prevent Redis memory to overflow.
- Consistent with the idempotency window used by major payment processors.
---

## Alternatives considered

### Store only the payment ID, not the full response

Only the payment ID is stored in cache as the payment result is already stored in the DB.

**Rejected because:**
- It requires an additional DB read for every cache hit. Not worth the added latency.

### Use an API gateway plugin (e.g. Kong)

Move the idempotency mechanism out of the service and into a gateway.

**Rejected because:**
- The gateway would have no access to the payment business logic and things like body mismatch validation could not be implemented.

### Synchronous lock via database advisory lock.

Execute a database lock at row level for the payment.

**Rejected because:**
- It couples the idempotency mechanism to the database engine, and it could cause potential contentions.

---

## Tradeoffs

- Redis becomes a **required runtime dependency**. If Redis is unavailable, we must decide: fail open (process the payment, risk duplication) or fail closed (return `503`). **Fail closed** is chosen to protect financial integrity.
- Developers must understand the two-phase logic (lock → process → cache) when modifying the payments handler.
- Clients that do not send an `Idempotency-Key` header will receive a `400 Bad Request`. This is a breaking change for any existing client and must be communicated via the API changelog.

---

## References

- [ADR001 - Kafka over REST](001-kafka-over-rest.md)