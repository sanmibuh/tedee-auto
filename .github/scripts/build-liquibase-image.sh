#!/usr/bin/env bash
#
# Builds the Liquibase migration image with the H2 driver jar resolved by Maven from pom.xml,
# so the migration image always uses the same H2 as the application.
#
# Usage: build-liquibase-image.sh <image-ref> [extra docker build args...]
set -euo pipefail

readonly IMAGE="${1:?usage: build-liquibase-image.sh <image-ref> [docker build args...]}"
shift

H2_VERSION="$(./mvnw -q -B help:evaluate -Dexpression=h2.version -DforceStdout)"
readonly H2_VERSION

# Maven resolves the exact jar the application uses and fails on a checksum mismatch (-C).
./mvnw -q -B -C dependency:copy \
  -Dartifact="com.h2database:h2:${H2_VERSION}:jar" \
  -DoutputDirectory=target/liquibase \
  -Dmdep.stripVersion=true

docker build \
  -f docker/liquibase/Dockerfile \
  --build-arg "H2_VERSION=${H2_VERSION}" \
  -t "${IMAGE}" \
  "$@" \
  .
