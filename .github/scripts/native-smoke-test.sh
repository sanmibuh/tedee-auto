#!/usr/bin/env bash
#
# Native image smoke test.
#
# Boots the given container image with the minimal configuration required to
# satisfy @ConfigurationProperties validation, then polls the Actuator health
# endpoint until the Spring context is fully up. A missing native-image
# reflection hint (or any other startup failure) aborts the context refresh, the
# container exits, and this script fails fast dumping the container logs — the
# exact class of failure that only surfaces in the native binary, not on the JVM.
#
# The application only validates the schema (Liquibase owns all DDL), so the migration image is run
# first against the same data directory, exactly as in deployment.
#
# Usage: native-smoke-test.sh <image-ref> <migration-image-ref>
set -euo pipefail

readonly IMAGE="${1:?usage: native-smoke-test.sh <image-ref> <migration-image-ref>}"
readonly MIGRATION_IMAGE="${2:?usage: native-smoke-test.sh <image-ref> <migration-image-ref>}"
readonly CONTAINER="tedee-native-smoke"
readonly HEALTH_URL="http://localhost:8080/actuator/health"
readonly TIMEOUT_SECONDS=90

DATA_DIR="$(mktemp -d)"
readonly DATA_DIR
# The distroless image runs as UID 65532; the H2 data directory must be writable by it.
chmod 777 "${DATA_DIR}"

cleanup() {
  docker rm -f "${CONTAINER}" >/dev/null 2>&1 || true
  rm -rf "${DATA_DIR}"
}
trap cleanup EXIT

fail() {
  echo "::error::Native smoke test failed: $1"
  echo "----- container logs -----"
  docker logs "${CONTAINER}" 2>&1 || true
  echo "--------------------------"
  exit 1
}

echo "Applying migrations from image: ${MIGRATION_IMAGE}"
docker run --rm --user 65532:65532 -v "${DATA_DIR}:/data" "${MIGRATION_IMAGE}" >/dev/null

echo "Starting container from image: ${IMAGE}"
docker run -d --name "${CONTAINER}" \
  -e TEDEE_HOST=localhost \
  -e TEDEE_API_KEY=smoke-test \
  -e TEDEE_CALLBACK_SECRET=smoke-test-callback-secret \
  -e PUBLIC_URL=http://localhost:8080 \
  -e TELEGRAM_BOT_TOKEN=smoke-test-bot-token \
  -e TELEGRAM_CHAT_ID=smoke-test-chat-id \
  -v "${DATA_DIR}:/home/nonroot/data" \
  -p 8080:8080 \
  "${IMAGE}" >/dev/null

echo "Waiting up to ${TIMEOUT_SECONDS}s for ${HEALTH_URL} to report healthy..."
for ((elapsed = 0; elapsed < TIMEOUT_SECONDS; elapsed++)); do
  if ! docker inspect -f '{{.State.Running}}' "${CONTAINER}" 2>/dev/null | grep -q true; then
    fail "container exited before becoming healthy"
  fi
  if curl -fsS "${HEALTH_URL}" 2>/dev/null | grep -q '"status":"UP"'; then
    echo "Native image booted and reported healthy after ${elapsed}s."
    exit 0
  fi
  sleep 1
done

fail "timed out after ${TIMEOUT_SECONDS}s waiting for a healthy status"
