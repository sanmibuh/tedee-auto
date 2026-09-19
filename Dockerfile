# Stage 1: compile GraalVM native binary (fully static, musl libc)
FROM ghcr.io/graalvm/native-image-community:25-muslib AS builder

WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -B -q

COPY openapi ./openapi
COPY src ./src

RUN ./mvnw -Pnative native:compile -B -DskipTests -q

# Stage 2: minimal distroless static runtime (non-root, CA certs included)
FROM gcr.io/distroless/static-debian12:nonroot

LABEL org.opencontainers.image.source=https://github.com/sanmibuh/tedee-auto

COPY --from=builder /app/target/tedee-automation /tedee-automation

USER nonroot

ENTRYPOINT ["/tedee-automation"]
