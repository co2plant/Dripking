# Backend Deployment

## Scope

The MVP backend is packaged as a Docker image and remains infrastructure-neutral. Runtime values are supplied through environment variables, not committed configuration.

## Build

```sh
docker build -t dripking-backend .
```

The Dockerfile builds the Spring Boot jar with Java 21 and starts it with the `prod` profile.

## Required Runtime Configuration

Use `.env.example` as the deployment template. Minimum production values:

- `SPRING_PROFILES_ACTIVE=prod`
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

```sh
docker run --rm --env-file .env.example -p 8080:8080 dripking-backend
```

For real deployment, pass an environment file with real secret values outside the repository. The application expects PostgreSQL and S3-compatible storage to be reachable from the container network.
When running the backend itself in Docker, `POSTGRES_HOST=localhost` means the backend container, so use the database service hostname or another address reachable from that container.
The image creates `/var/log/dripking` for the non-root runtime user; keep `LOG_PATH` pointed at a writable path.
