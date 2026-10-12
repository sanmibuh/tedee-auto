import json
import sys
from datetime import date, datetime, timedelta, timezone

RETENTION_DAYS = 90


def order_models(models, records):
  outcomes = {model: {"ok": 0, "total": 0} for model in models}
  for record in records:
    if record["model"] not in models:
      continue
    outcomes[record["model"]]["total"] += 1
    if record["outcome"] == "ok":
      outcomes[record["model"]]["ok"] += 1

  def success_rate(model):
    model_outcomes = outcomes[model]
    if model_outcomes["total"] == 0:
      return 0
    return model_outcomes["ok"] / model_outcomes["total"]

  return sorted(models, key=success_rate, reverse=True)


def retain_recent_records(records, today):
  cutoff = date.fromisoformat(today) - timedelta(days=RETENTION_DAYS)
  return [record for record in records if date.fromisoformat(record["date"]) >= cutoff]


if __name__ == "__main__":
  if len(sys.argv) < 3:
    print("Usage: model_strategy.py <model-stats.json> <model>...", file=sys.stderr)
    sys.exit(1)

  with open(sys.argv[1]) as stats_file:
    records = retain_recent_records(
        json.load(stats_file),
        datetime.now(timezone.utc).date().isoformat())

  for model in order_models(sys.argv[2:], records):
    print(model)
