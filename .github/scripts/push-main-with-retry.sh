#!/usr/bin/env bash
# Push the current HEAD to main, rebasing onto any concurrent update, with a
# bounded retry. Works from a detached HEAD (actions/checkout leaves the repo
# detached even for a branch ref), so it rebases the explicit remote ref and
# pushes HEAD:main instead of relying on the current branch. Fails (non-zero) if
# every attempt fails, so a caller can never proceed as if the push succeeded.
set -euo pipefail

for attempt in 1 2 3; do
  git fetch origin main
  if git rebase origin/main && git push origin HEAD:main; then
    exit 0
  fi
  git rebase --abort 2>/dev/null || true
  echo "Push attempt ${attempt} failed, retrying..."
  sleep 5
done

echo "ERROR: failed to push to main after 3 attempts."
exit 1
