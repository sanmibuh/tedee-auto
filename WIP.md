# WIP — Issue #95: primary adapter (cron) to publish CloseLockCommand

## Decision
Primary (driving) adapter = configurable cron scheduler. Each configured lock has its own cron expression.

Config shape (`Map<Integer, String>`, deviceId → cron):

```yaml
sanmibuh:
  scheduler:
    lock:
      schedules:
        12345: "0 0 0,22 * * *"   # 00:00 and 22:00 every day
```

## Package split (this PR)
`infrastructure` is split into `primary` / `secondary` slices, motivated by the first primary adapter (the scheduler):
- `lock/infrastructure/primary/`   → LockScheduler, LockSchedulingConfigurer, LockSchedulerProperties
- `lock/infrastructure/secondary/` → TedeeLockRepository, TedeeClientConfiguration, TedeeProperties, TedeeApiAdapter
ArchUnit enforcement of the slice dependency rules is deferred to #106.

## Plan
1. `LockScheduler` — publishes `CloseLockCommand(deviceId)` to the `CommandBus` for a given deviceId. (RED/GREEN)
2. `LockSchedulerProperties` — `@ConfigurationProperties(prefix = "sanmibuh.scheduler.lock")` holding `Map<Integer, String> schedules`.
3. `LockSchedulingConfigurer` — implements `SchedulingConfigurer`, registers one `CronTask` per schedule entry that invokes `LockScheduler.closeLock(deviceId)`.
4. Move existing secondary adapters into `secondary` package (`git mv`).
5. `application.yml` — document/sample schedules block.
6. Update `ARCHITECTURE.md` with the primary adapter and the primary/secondary split.

## Steps
- [ ] Step 1: LockScheduler dispatches CloseLockCommand
- [ ] Step 2: LockSchedulerProperties
- [ ] Step 3: LockSchedulingConfigurer registers cron tasks
- [ ] Step 4: move new scheduler + existing adapters into primary/secondary packages
- [ ] Step 5: application.yml + ARCHITECTURE.md
- [ ] `./mvnw verify` green, `make pitest` 100%
