import unittest

from model_strategy import order_models, retain_recent_records


class ModelStrategyTest(unittest.TestCase):
  def test_should_preserveQualityOrder_whenModelsMeetReliabilityThreshold(self):
    models = ["pro", "flash", "lite"]
    records = [
        {"date": "2026-10-10", "model": "pro", "outcome": "ok"},
        {"date": "2026-10-10", "model": "pro", "outcome": "nok"},
        {"date": "2026-10-10", "model": "flash", "outcome": "ok"},
        {"date": "2026-10-10", "model": "lite", "outcome": "ok"},
    ]

    self.assertEqual(["pro", "flash", "lite"], order_models(models, records))

  def test_should_demoteUnreliableModels_whenStatisticsAreAvailable(self):
    models = ["pro", "flash", "lite"]
    records = [
        {"date": "2026-10-10", "model": "pro", "outcome": "nok"},
        {"date": "2026-10-10", "model": "flash", "outcome": "ok"},
        {"date": "2026-10-10", "model": "lite", "outcome": "ok"},
    ]

    self.assertEqual(["flash", "lite", "pro"], order_models(models, records))

  def test_should_discardRecordsOlderThan90Days_whenRetentionIsApplied(self):
    records = [
        {"date": "2026-07-13", "model": "flash", "outcome": "ok"},
        {"date": "2026-07-14", "model": "flash", "outcome": "ok"},
    ]

    self.assertEqual(
        [{"date": "2026-07-14", "model": "flash", "outcome": "ok"}],
        retain_recent_records(records, "2026-10-12"))


if __name__ == "__main__":
  unittest.main()
