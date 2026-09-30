# WIP — Issue #242: register Bridge callback idempotently on startup, unregister on shutdown

## Decisions
- `TedeeCallbackRegistration` (`SmartLifecycle`, `lock/infrastructure/secondary`) uses the generated `CallbackApi`. Never `PUT /callback`.
- Registration is idempotent by URL: `start()` does `GET /callback` first; none matching → `POST`, one → reuse its id, several → keep one and delete the rest. Only entries matching our URL are touched.
- Callback URL = application public base URL + fixed path of the webhook controller (#244): `/tedee/events`.
- Public base URL is a general, non-Tedee setting: `sanmibuh.server.public-url` bound from mandatory env var `PUBLIC_URL` (`ServerProperties` record in `org.sanmibuh.tedee`).
- Registration failure at startup → log WARN and keep running. Periodic re-registration, 404/409 handling → #243.
- Bridge returns `id` as `Long`, `DELETE /callback/{id}` takes `Integer` → `Math.toIntExact`.
- Tests: `@RestClientTest` + `MockRestServiceServer`; the sut is built manually from the `CallbackApi` bean so auto-startup does not fire requests before expectations are set.
- Open: verify whether unresolved `${...}` placeholders really fail fast (suspected they bind literally).

## Steps
1. ~~RED/GREEN: `start()` posts `{url, method: POST}` to `/callback`.~~
2. ~~RED/GREEN: `start()` lists callbacks first and posts only when none matches our URL (adjust step 1 test; add "already registered → no POST").~~
3. ~~RED/GREEN: `start()` deletes duplicate entries matching our URL.~~
3b. ~~REFACTOR: extract `ourCallbacks(...)` lookup (and callback URL) into private methods.~~
4. ~~RED/GREEN: `stop()` deletes `/callback/{id}` with the kept id (posted or reused).~~
5. ~~RED/GREEN: `stop()` does nothing when not registered; `isRunning()` reflects state.~~
6. ~~RED/GREEN: `start()` logs WARN and does not throw when the bridge fails.~~
7. ~~RED/GREEN: `ServerProperties` validation (blank/missing `public-url` fails startup).~~
7b. ~~Wiring: `ServerConfiguration` enables `ServerProperties`, `TedeeCallbackRegistration` as `@Component`, `application.yml` (`public-url: ${PUBLIC_URL:}`), smoke test `PUBLIC_URL`, `CallbackApi` mocked in `@SpringBootTest`.~~
   - Unresolved `${...}` placeholders bind literally (verified): `TEDEE_HOST`/`TEDEE_API_KEY` do NOT fail fast → separate issue pending user confirmation.
   - Local/deploy (git-ignored) configs updated: `public-url`; deploy compose publishes `9001:8080` + `stop_grace_period: 30s`.
8. RED/GREEN: reflection hints for `ServerProperties`.
9. Graceful shutdown (`server.shutdown: graceful`), smoke test `PUBLIC_URL` env var, `ARCHITECTURE.md`.
10. `make format`, `./mvnw verify`, `make pitest`.
