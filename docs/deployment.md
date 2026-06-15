# Backend Deployment

## Scope

The MVP backend is packaged as a Docker image. Local infrastructure is Docker-first: Docker Compose starts PostgreSQL and the backend with environment-driven configuration.

## Local Docker Run

```sh
docker compose up --build
```

This starts:

- `postgres`: PostgreSQL 16 with a persistent Docker volume
- `backend`: the Spring Boot API on `http://localhost:8080`

With the `dev` profile, Hibernate creates or updates the schema first and then Spring Boot runs `src/main/resources/db/seed/dev-seed.sql` against the Docker PostgreSQL database. The seed SQL is idempotent for a persistent local volume.

For host-side JVM development with the same database:

```sh
docker compose up -d postgres
sh gradlew bootRun
```

To reset local data, remove the database volume:

```sh
docker compose down -v
```

## Build Image

```sh
docker build -t dripking-backend .
```

The Dockerfile builds the Spring Boot jar with Java 21 and defaults to the `prod` profile. Docker Compose overrides that to `dev` for local seed data.

## Required Runtime Configuration

Use `.env.example` as the local Docker Compose template. For production, keep secrets outside the repository and override at least:

- `SPRING_PROFILES_ACTIVE`
- `POSTGRES_HOST`
- `POSTGRES_PORT`
- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION`
- `CORS_ALLOWED_ORIGINS`
- `S3_ENDPOINT`
- `S3_REGION`
- `S3_BUCKET`
- `S3_ACCESS_KEY`
- `S3_SECRET_KEY`
- `S3_PUBLIC_BASE_URL`
- `S3_KEY_PREFIX`
- `LOG_PATH`

## Run

Prefer Docker Compose for local development. For a pre-built image in another container platform:

```sh
docker run --rm --env-file .env.example -p 8080:8080 dripking-backend
```

For real deployment, pass an environment file with real secret values outside the repository and set `SPRING_PROFILES_ACTIVE=prod`. The application expects PostgreSQL and S3-compatible storage to be reachable from the container network.
When running the backend itself in Docker, `POSTGRES_HOST=localhost` means the backend container, so use the database service hostname or another address reachable from that container.
The image creates `/var/log/dripking` for the non-root runtime user; keep `LOG_PATH` pointed at a writable path.
