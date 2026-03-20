# Local setup

For local deployment purposes each components will be deployed in a separate container in Docker.

## Prerequisites
- Java 17
- Maven
- Docker
- Docker Compose

## Run sequence
### 1) env files

- Create .postgres_env file under infra/docker with the following content:
```bash
POSTGRES_USER=<your_desired_user>
POSTGRES_PASSWORD=<your_desired_password>
POSTGRES_DB=cloud-payment-platform
```
- Create .services_env file under infra/docker with the following content:

```bash
SPRING_PROFILES_ACTIVE=docker

POSTGRES_URL=postgresql://postgresql:5432
POSTGRES_USER=<your_desired_user>
POSTGRES_PASSWORD=<your_desired_password>
```

### 1) Run PostgreSQL container
```bash
docker compose -f infra/docker/docker-compose.postgresql.yml up -d
```

### 2) Run cloud-payment-platform web services
```bash
docker compose -f infra/docker/docker-compose.local.yml up -d
```