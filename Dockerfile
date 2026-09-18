# Stage 1: compile GraalVM native binary
FROM ghcr.io/graalvm/native-image-community:25 AS builder

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -B -q

COPY openapi ./openapi
COPY src ./src

RUN ./mvnw -Pnative native:compile -B -DskipTests -q

# Stage 2: minimal distroless runtime (non-root, CA certs included)
FROM gcr.io/distroless/base-debian12:nonroot

LABEL org.opencontainers.image.source=https://github.com/sanmibuh/tedee-auto

COPY --from=builder /app/target/tedee-automation /tedee-automation

USER nonroot

ENTRYPOINT ["/tedee-automation"]
