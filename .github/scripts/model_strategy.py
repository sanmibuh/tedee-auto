import json
import sys
from collections import defaultdict
from datetime import date, timedelta

RELIABILITY_THRESHOLD = 0.2
RETENTION_DAYS = 90


def order_models(models, records):
  outcomes = defaultdict(lambda: {"ok": 0, "total": 0})
  for record in records:
    if record["model"] not in models:
      continue
    outcomes[record["model"]]["total"] += 1
    if record["outcome"] == "ok":
      outcomes[record["model"]]["ok"] += 1

  reliable_models = []
  unreliable_models = []
  for model in models:
    model_outcomes = outcomes[model]
    is_reliable = (model_outcomes["total"] == 0 or
                   model_outcomes["ok"] / model_outcomes["total"] >= RELIABILITY_THRESHOLD)
    (reliable_models if is_reliable else unreliable_models).append(model)
  return reliable_models + unreliable_models


def retain_recent_records(records, today):
  cutoff = date.fromisoformat(today) - timedelta(days=RETENTION_DAYS)
  return [record for record in records if date.fromisoformat(record["date"]) >= cutoff]


if __name__ == "__main__":
  if len(sys.argv) < 3:
    print("Usage: model_strategy.py <model-stats.json> <model>...", file=sys.stderr)
    sys.exit(1)

  with open(sys.argv[1]) as stats_file:
    records = json.load(stats_file)

  for model in order_models(sys.argv[2:], records):
    print(model)
