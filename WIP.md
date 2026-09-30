# WIP — Issue #242: register Bridge callback on startup, unregister on shutdown

## Decisions
- `TedeeCallbackRegistration` (`SmartLifecycle`, `lock/infrastructure/secondary`) uses the generated `CallbackApi`. Never `PUT /callback`.
- Callback URL = application public base URL + fixed path of the (future) webhook controller (`/tedee/events`).
- Public base URL is a general, non-Tedee setting: `sanmibuh.server.public-url` bound from mandatory env var `PUBLIC_URL` (`ServerProperties` record in `org.sanmibuh.tedee`).
- Registration failure at startup → log WARN and keep running (the lock cron must not depend on webhooks). Periodic re-registration is deferred to the edge-cases follow-up issue.
- Bridge returns `id` as `Long`, `DELETE /callback/{id}` takes `Integer` → `Math.toIntExact`.
- Tests: `@RestClientTest` + `MockRestServiceServer`; the sut is built manually from the `CallbackApi` bean so auto-startup does not fire requests before expectations are set.

## Steps
1. RED/GREEN: `start()` posts `{url, method: POST}` to `/callback`.
2. RED/GREEN: `stop()` deletes `/callback/{id}` with the id returned by `start()`.
3. RED/GREEN: `stop()` does nothing when not registered; `isRunning()` reflects state.
4. RED/GREEN: `start()` logs WARN and does not throw when the bridge fails.
5. RED/GREEN: `ServerProperties` validation (blank/missing `public-url` fails startup) + wiring + `application.yml`.
6. RED/GREEN: reflection hints for `ServerProperties`.
7. Graceful shutdown (`server.shutdown: graceful`), smoke test env var, `ARCHITECTURE.md`.
8. `make format`, `./mvnw verify`, `make pitest`.
