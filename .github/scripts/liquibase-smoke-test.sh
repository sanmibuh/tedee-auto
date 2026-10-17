#!/usr/bin/env bash
#
# Migration image smoke test.
#
# Runs the image as the application's UID (65532) against an empty writable data directory and
# fails unless the migration completes and creates the H2 database file.
#
# Usage: liquibase-smoke-test.sh <image-ref>
set -euo pipefail

readonly IMAGE="${1:?usage: liquibase-smoke-test.sh <image-ref>}"

DATA_DIR="$(mktemp -d)"
readonly DATA_DIR
trap 'rm -rf "${DATA_DIR}"' EXIT
chmod 777 "${DATA_DIR}"

docker run --rm --user 65532:65532 -v "${DATA_DIR}:/data" "${IMAGE}"

[[ -f "${DATA_DIR}/tedee.mv.db" ]] || {
  echo "::error::Migration image did not create tedee.mv.db"
  exit 1
}
echo "Migration image applied the changelog and created the database."
