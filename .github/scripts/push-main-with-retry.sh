#!/usr/bin/env bash
# Push the current branch to main, rebasing onto any concurrent updates, with a
# bounded retry. Fails (non-zero) if every attempt fails, so a caller can never
# proceed as if the push had succeeded.
set -euo pipefail

for attempt in 1 2 3; do
  if git pull --rebase origin main && git push origin main; then
    exit 0
  fi
  echo "Push attempt ${attempt} failed, retrying..."
  sleep 5
done

echo "ERROR: failed to push to main after 3 attempts."
exit 1
