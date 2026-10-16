#!/usr/bin/env bash
#
# Builds the Liquibase migration image with the SQLite driver version resolved from pom.xml,
# so the migration image always uses the same sqlite-jdbc as the application.
#
# Usage: build-liquibase-image.sh <image-ref> [extra docker build args...]
set -euo pipefail

readonly IMAGE="${1:?usage: build-liquibase-image.sh <image-ref> [docker build args...]}"
shift

SQLITE_JDBC_VERSION="$(./mvnw -q -B help:evaluate -Dexpression=sqlite-jdbc.version -DforceStdout)"
readonly SQLITE_JDBC_VERSION

docker build \
  -f docker/liquibase/Dockerfile \
  --build-arg "SQLITE_JDBC_VERSION=${SQLITE_JDBC_VERSION}" \
  -t "${IMAGE}" \
  "$@" \
  .
