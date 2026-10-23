#!/usr/bin/env bash
#
# Generates the next Liquibase changeset by diffing the JPA entity schema against the current changelog.
# The `migration` Maven profile makes Liquibase diff the JPA entities (read by liquibase-hibernate7) against
# the local database; this script names and signs the result. The generated file is a starting point: review it before committing.
#
# The diff runs against the local development database (DB_DIR, default data), which must already have the
# existing migrations applied (`make execute-migration`, which `make create-migration` runs first).
#
# The file is named <YYYYMMDDHHmmss>-GH<issue>.h2.sql so changesets sort chronologically. The issue number is
# taken from the branch name (<type>/GH-<issue>-<slug>).
#
# Usage: [DB_DIR=data] generate-migration.sh
set -euo pipefail

readonly CHANGES="db/changelog/changes"
readonly WORK="target/migration"
readonly DB_DIR="${DB_DIR:-data}"
readonly BRANCH_PATTERN='^[a-z]+/GH-([0-9]+)-'

branch="$(git branch --show-current)"
if [[ ! "${branch}" =~ ${BRANCH_PATTERN} ]]; then
  echo "branch '${branch}' does not match <type>/GH-<issue>-<name>" >&2
  exit 1
fi
readonly ISSUE="${BASH_REMATCH[1]}"
readonly NAME="$(date -u +%Y%m%d%H%M%S)-GH${ISSUE}"
readonly TARGET="${CHANGES}/${NAME}.h2.sql"

rm -rf "${WORK}"
mkdir -p "${WORK}/out"

mkdir -p "${DB_DIR}"
DB_PATH="$(cd "${DB_DIR}" && pwd)/tedee"
readonly DB_PATH

./mvnw -q -B -Pmigration process-classes -Dmigration.name="${NAME}" -Ddb.path="${DB_PATH}"

if [[ ! -f "${WORK}/out/${NAME}.h2.sql" ]]; then
  echo ">> No schema changes: the JPA entities already match db/changelog"
  exit 0
fi

AUTHOR="$(git config user.name | tr -d ' ' | tr '[:upper:]' '[:lower:]')"
sed "s/^-- changeset [^:]*:/-- changeset ${AUTHOR:-unknown}:/" "${WORK}/out/${NAME}.h2.sql" > "${TARGET}"
echo ">> Generated ${TARGET} - review it before committing"
