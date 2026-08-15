# tedee-auto

Backend service that automates a [Tedee](https://tedee.com) smart lock — built with Spring Boot and compiled to a GraalVM native image.

## Requirements

- Java 25 (GraalVM recommended)
- Maven wrapper included (`./mvnw`)
- Docker (for native image build)

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

| Concern        | Choice                          |
|----------------|---------------------------------|
| Runtime        | Java 25 / GraalVM Native Image  |
| Framework      | Spring Boot 4.1                 |
| Architecture   | Hexagonal (Ports & Adapters)    |
| Design         | DDD + CQRS                      |
| Tests          | JUnit 5 · JaCoCo · PITest       |
| Architecture tests | ArchUnit                    |
| Container registry | GitHub Container Registry   |

## CI

Every pull request (non-draft) runs:

1. Build and tests
2. JaCoCo coverage report — diff vs `main`
3. PITest mutation testing (incremental) — diff vs `main`

Results are posted as a comment on the PR. Baseline metrics are stored in the orphan branch `coverage-data`.

On merge to `main`, the updated metrics and PITest history are saved back to `coverage-data`.

## Architecture

See [ARCHITECTURE.md](ARCHITECTURE.md) for module design, data flow and key decisions.

## License

[MIT](LICENSE)
