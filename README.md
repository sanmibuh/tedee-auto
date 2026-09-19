[![Latest release](https://img.shields.io/github/v/release/sanmibuh/tedee-auto?sort=semver)](https://github.com/sanmibuh/tedee-auto/releases/latest)
[![Coverage](https://img.shields.io/endpoint?url=https%3A%2F%2Fraw.githubusercontent.com%2Fsanmibuh%2Ftedee-auto%2Fcoverage-data%2Fcoverage-badge.json)](https://sanmibuh.github.io/tedee-auto/coverage/)
[![Mutation score](https://img.shields.io/endpoint?url=https%3A%2F%2Fraw.githubusercontent.com%2Fsanmibuh%2Ftedee-auto%2Fcoverage-data%2Fmutation-badge.json)](https://sanmibuh.github.io/tedee-auto/mutation/)

Backend service that automates a [Tedee](https://tedee.com) smart lock — built with Spring Boot and compiled to a GraalVM native image.

## Requirements

- Java 25 (`openjdk-25.0.3+tzdata2026b`) — managed via [jenv](https://www.jenv.be), version pinned in `.java-version`
- Maven wrapper included (`./mvnw`)
- Docker (for native image build)

## Development setup

Install the required JDK, register it with jenv, and rehash:

```bash
jenv add <path-to-jdk>   # register openjdk-25.0.3+tzdata2026b
jenv rehash              # .java-version drives version selection automatically
```

If using [OpenCode](https://opencode.ai) as AI assistant, install the Java LSP for real-time diagnostics:

```bash
brew install jdtls
```

## Quick start

```bash
make build    # compile
make test     # run tests + coverage report
make pitest   # run mutation tests (incremental)
```

Or directly with Maven:

```bash
./mvnw verify
```

## Tech stack

| Concern            | Choice                         |
|--------------------|--------------------------------|
| Runtime            | Java 25 / GraalVM Native Image |
| Framework          | Spring Boot 4.1                |
| Architecture       | Hexagonal (Ports & Adapters)   |
| Design             | DDD + CQRS                     |
| Tests              | JUnit 5 · JaCoCo · PITest      |
| Architecture tests | ArchUnit                       |
| Container registry | GitHub Container Registry      |

## CI

Every pull request (non-draft) runs:

1. Build and tests
2. JaCoCo coverage report — diff vs `main`
3. PITest mutation testing (incremental) — diff vs `main`

Results are posted as a comment on the PR. Baseline metrics are stored in the orphan branch `coverage-data`.

On merge to `main`, the updated metrics and PITest history are saved back to `coverage-data`, and the JaCoCo and PITest HTML reports are published to [GitHub Pages](https://sanmibuh.github.io/tedee-auto/) (linked from the coverage and mutation badges above).

## Incremental mutation testing

`make pitest` is the canonical local command for mutation testing and mirrors the CI incremental strategy. It:

1. fetches the orphan `coverage-data` branch,
2. restores its `pitest-history.bin` into `.pit/history-input.bin`,
3. runs `./mvnw test-compile pitest:mutationCoverage -B`, reusing that history so PITest only re-evaluates changed code,
4. leaves the fresh history in `.pit/history-output.bin` for inspection.

The history contract is identical between local and CI: the file is stored as `pitest-history.bin` on `coverage-data`, consumed as `.pit/history-input.bin` and produced as `.pit/history-output.bin`. On merge to `main`, CI copies the new `.pit/history-output.bin` back to `coverage-data:pitest-history.bin`, which becomes the baseline for the next local or CI run.

On the first run — or whenever `coverage-data` is missing, cannot be fetched, or does not yet contain a usable `pitest-history.bin` — `make pitest` prints that it is running a full baseline and evaluates every mutant. This is the deliberate, documented fallback; it is never silent.

## Deployment

The service is published as a GraalVM native image to the GitHub Container Registry: `ghcr.io/sanmibuh/tedee-auto/tedee-automation`. Browse all published versions on the [package page](https://github.com/sanmibuh/tedee-auto/pkgs/container/tedee-auto%2Ftedee-automation) and the corresponding changelogs under [Releases](https://github.com/sanmibuh/tedee-auto/releases).

The container's working directory is `/home/nonroot`, so Spring Boot automatically picks up `./config/application.yml`. Mount your config directory (read-only) at `/home/nonroot/config` and a writable directory at `/var/log/tedee` for the rolling log files.

### 1. Prepare the host

All examples below use `/srv/tedee-auto` as the base host path. Replace every occurrence with your own path (config file, Compose volumes and the `tail` command must all point to the same base). If you don't have root, pick a base under your home directory (e.g. `$HOME/tedee-auto`) instead.

```bash
# Creating directories under a system path such as /srv requires root; drop sudo
# if you use a user-writable base path.
sudo mkdir -p /srv/tedee-auto/config /srv/tedee-auto/logs

# The distroless image runs as the non-root user (UID/GID 65532); the log
# directory must be owned by it. chown always needs elevated privileges.
sudo chown -R 65532:65532 /srv/tedee-auto/logs
```

Then create `/srv/tedee-auto/config/application.yml` with the content from the next step (edit it with `sudo` if the directory is root-owned).

### 2. `config/application.yml`

```yaml
spring:
  application:
    name: tedee-automation

logging:
  file:
    name: /var/log/tedee/tedee-automation.log
  logback:
    rollingpolicy:
      file-name-pattern: /var/log/tedee/tedee-automation-%d{yyyy-MM-dd}.%i.log.gz
      max-file-size: 10MB
      max-history: 30
      total-size-cap: 500MB
      clean-history-on-start: true

sanmibuh:
  rest:
    tedee:
      base-url: http://<tedee-bridge-ip>/v1.0
      api-key: <your-api-key>
  scheduler:
    lock:
      schedules:
        # Map key is the numeric lock device id; value is a Spring cron expression.
        # Replace 12345 with your lock id. Example below closes the lock daily at 21:30 and 23:30.
        12345: "0 30 21,23 * * *"
```

### 3. `docker-compose.yml`

```yaml
services:
  tedee-automation:
    image: ghcr.io/sanmibuh/tedee-auto/tedee-automation:latest
    container_name: tedee-automation
    restart: unless-stopped
    volumes:
      - /srv/tedee-auto/config:/home/nonroot/config:ro
      - /srv/tedee-auto/logs:/var/log/tedee
    logging:
      driver: json-file
      options:
        max-size: "10m"
        max-file: "3"
```

> Use `:latest` for the newest build, or pin a specific version tag (e.g. `:0.1.1`) for reproducible deployments. Available tags are listed on the [package page](https://github.com/sanmibuh/tedee-auto/pkgs/container/tedee-auto%2Ftedee-automation) and [releases](https://github.com/sanmibuh/tedee-auto/releases).

### 4. Run

```bash
docker compose up -d
```

Follow the logs (each command runs in the foreground, so use a separate terminal for each):

```bash
docker compose logs -f                               # container stdout
tail -f /srv/tedee-auto/logs/tedee-automation.log    # rolling file log
```

## Architecture

See [ARCHITECTURE.md](ARCHITECTURE.md) for module design, data flow and key decisions.

## License

[MIT](LICENSE)
