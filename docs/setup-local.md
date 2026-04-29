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

  - `POSTGRES_USER=<your_desired_user>`
  - `POSTGRES_PASSWORD=<your_desired_password>`
  - `POSTGRES_DB=cloud-payment-platform`


- Create .services_env file under infra/docker with the following content:

  - `SPRING_PROFILES_ACTIVE=docker`
  - `POSTGRES_URL=postgresql://postgresql:5432`
  - `POSTGRES_USER=<your_desired_user>`
  - `POSTGRES_PASSWORD=<your_desired_password>`
  - `TOKEN_SECRET=<your_desired_secret>`

### 1) Run PostgreSQL container
`docker compose -f infra/docker/docker-compose.postgresql.yml up -d`

### 2) Run Kafka and Kafka UI containers
`docker compose -f infra/docker/docker-compose.kafka.yml up -d`

### 3) Run cloud-payment-platform web services
`docker compose -f infra/docker/docker-compose.local.yml up -d`