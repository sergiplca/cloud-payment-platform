# Local setup

> This document describes how to run the full platform locally and verify each part of the system works correctly.

For local deployment purposes each components will be deployed in a separate container in Docker.

## Prerequisites
- Docker and Docker Compose
- Java 17
- A REST client (curl, Postman, or IntelliJ HTTP client)

## Starting the platform

All infrastructure and services are defined in `infra/docker/`. A single `docker-compose.yml` file is in place to run everything at once.

Create `.postgres_env` file under `infra/docker/` with the following content:

  - `POSTGRES_USER=<your_desired_user>`
  - `POSTGRES_PASSWORD=<your_desired_password>`
  - `POSTGRES_DB=cloud-payment-platform`


Create `.services_env` file under `infra/docker/` with the following content:

  - `SPRING_PROFILES_ACTIVE=docker`
  - `POSTGRES_URL=postgresql://postgresql:5432`
  - `POSTGRES_USER=<your_desired_user>`
  - `POSTGRES_PASSWORD=<your_desired_password>`
  - `TOKEN_SECRET=<your_desired_secret>`

```bash
docker compose up -d
```

This starts:

| Container            | Port | Purpose              |
|----------------------|------|----------------------|
| PostgreSQL           | 5432 | Primary database     |
| Kafka                | 9092 | Event broker         |
| Kafka UI             | 9080 | Kafka Management UI  |
| Redis                | 6379 | Memory Cache         |
| Redis UI             | 5540 | Redis Management UI  |
| api-gateway          | 8080 | External entry point |
| order-service        | 8081 | Order management     |
| payment-service      | 8082 | Payment processing   |
| notification-service | 8083 | Async event consumer |

Verify all containers are running:

```bash
docker compose ps
```

---

## API documentation

Interactive Swagger UI is available through the gateway once services are running:

```
http://localhost:8080/swagger-ui.html
```

---

## End-to-end flow: payment creation

The full synchronous + asynchronous flow is:

```
POST /auth/roken   →  api-gateway (requests access token)
POST /v1/orders    →  order-service (creates order)
POST /v1/payments  →  payment-service (validates order, creates payment, publishes event)
                                ↓
                        Kafka: payments.payment.created
                                ↓
                    notification-service (consumes event, persists notification)
```

### Step 1 — Obtain a token

```bash
curl -s -X POST http://localhost:8080/auth/token \
  -H "Content-Type: application/json"
  -d '{
        "username": "dummy",
        "roles": ["USER"]
      }'
```

Copy the token from the response. All subsequent requests require it.

### Step 2 — Create an order

```bash
curl -s -X POST http://localhost:8080/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
        "amount": "0.1",
        "currency": "EUR",
        "customerReference": "dummy reference"
      }'
```

Copy the `id` from the response. You will need it in the next step.

### Step 3 — Create a payment

```bash
curl -s -X POST http://localhost:8080/v1/payments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
        "amount": "0.1",
        "currency": "EUR",
        "customerReference": "aaa",
        "orderId": <order-id-from-step-2>
      }'
```

Expected response: HTTP 201 with a payment object containing `"status": "CREATED"`.

---

## Verifying the async flow

### Option A — Check notification-service logs

```bash
docker compose logs notification-service --follow
```

Within a few seconds of creating the payment you should see a log line confirming the event was received and the notification was persisted. The log should include the `eventId` from the `payments.payment.created` event.

### Option B — Query the database directly

Connect to PostgreSQL:

```bash
docker exec -it <postgres-container-name> psql -U <username> -d <database>
```

Check that a notification record was created:

```sql
SELECT * FROM notificationservice.payment_notification ORDER BY creation_timestamp DESC LIMIT 5;
```

The most recent row should correspond to the payment you just created.

### Option C — Inspect the Kafka topic

Using your desired browser, navigate to `http://localhost:9080` and check the `payments.payment.created` contains the payment event you just created.

You can also access Kafka directly if you have the Kafka CLI tools available inside the container:
```bash
docker exec -it <kafka-container-name> \
  kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic payment.created \
  --from-beginning
```

This will print all events published to `payments.payment.created` since the topic was created. Verify the event structure matches the schema documented in [docs/events.md](events.md).

---

## Stopping the platform

```bash
docker compose down
```

To also remove volumes (database data, Kafka offsets, Redis keys):

```bash
docker compose down -v
```

---

## Troubleshooting

**A service fails to start:** check `docker compose logs <service-name>` for the error. The most common cause is a port conflict or a service starting before its dependency is ready.

**The payment request returns a 404 for the order:** verify the order was created successfully in Step 2 and that you copied the correct `id`.

**No notification record appears in the database:** check that `notification-service` started cleanly and is connected to both Kafka and PostgreSQL. Run `docker compose logs notification-service` to inspect.