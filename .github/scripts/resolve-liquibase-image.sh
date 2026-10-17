#!/usr/bin/env bash
#
# Decides whether the migration image must be rebuilt for a release or can be reused.
#
# The previous release's image is reused (only re-tagged) when it exists in the registry,
# db/ and docker/liquibase/ are unchanged since the previous release tag, and its h2.version
# label matches the H2 version resolved from pom.xml. Otherwise a new image is required.
#
# Usage: resolve-liquibase-image.sh <image-repository>
# Prints `reuse=<true|false>` and `previous=<previous version or empty>` (GITHUB_OUTPUT format).
set -euo pipefail

readonly REPOSITORY="${1:?usage: resolve-liquibase-image.sh <image-repository>}"

PREV_TAG="$(git tag --sort=-version:refname | head -1)"
readonly PREV_TAG
PREV_VERSION="${PREV_TAG#v}"
readonly PREV_VERSION

reuse() {
  [[ -n "${PREV_TAG}" ]] || return 1
  git diff --quiet "${PREV_TAG}" HEAD -- db docker/liquibase || return 1
  docker pull --quiet "${REPOSITORY}:${PREV_VERSION}" >/dev/null 2>&1 || return 1

  local current previous
  current="$(./mvnw -q -B help:evaluate -Dexpression=h2.version -DforceStdout)"
  previous="$(docker inspect --format '{{ index .Config.Labels "h2.version" }}' "${REPOSITORY}:${PREV_VERSION}")"
  [[ "${current}" == "${previous}" ]]
}

if reuse; then
  echo "reuse=true"
  echo "previous=${PREV_VERSION}"
else
  echo "reuse=false"
  echo "previous="
fi
