# WIP — Issue #42: Close Lock Use Case

## Real contract (source of truth)
Spec downloaded to `openapi/tedee-bridge-api.json` (Tedee Bridge API 1.2), fetched from
`http://10.0.1.24` → Swagger UI loads `spec.json` from the Tedee blob storage.

- Base path (server): `/v1.0`
- Endpoint: `POST /lock/{deviceId}/lock`
- `deviceId`: path param, `integer` (int32)
- Auth: `apiKey` in **header `api_token`** (NOT `Authorization: PersonalKey` — the old WIP assumption was wrong)
- Success response: `204 No Content`
- Error responses: 401 InvalidToken, 404 DeviceNotFound, 405 DeviceDisconnected, 406 DeviceBleError

## Design decision — generated client as adapter collaborator
- The Tedee API client is **generated with openapi-generator** (`7.25.0`) from the local spec (offline, no network needed to build).
- Generator flavor: `java` + **`library=restclient`** (Spring `RestClient` — idiomatic for Spring Boot 4 / Spring 7).
  - NOTE: openapi-generator `7.14.0`'s `restclient` template was broken on Spring 7 (`HttpHeaders` no longer implements `Map`).
    `7.25.0` fixes it, so we use the idiomatic `RestClient` transport instead of the earlier `native` fallback.
- Extra deps required by the generated client: `jackson-datatype-jsr310`, `org.openapitools:jackson-databind-nullable`.
- Generated code lives in package `${tedee.client.package}` = `com.tedee.bridge.client.*` (outside `org.sanmibuh`) so NullAway/ErrorProne treat it as unannotated third-party code. Output: `target/generated-sources/openapi` (not under `src/`, so Spotless ignores it).
- The generated client is a **collaborator of the secondary adapter** (`TedeeApiAdapter`), NOT the port itself.
- The domain secondary port (`LockPort`) stays decoupled from the generated model; the adapter maps between domain and generated client.
- Auth: the generated `ApiClient` registers `api_token` as `ApiKeyAuth("header", "api_token")` and applies it automatically — no manual interceptor; just configure the api key.
- `LockApi.postLock(Integer deviceId)` → `POST {baseUri}/lock/{deviceId}/lock`; `basePath = "/v1.0"` baked in. `ApiClient` is built from an injectable `RestClient`.
- Still TODO: GraalVM native reflection hints for the generated Jackson models.

## Known blocker (pre-existing, project-wide)
- `spotless:check` FAILS on the already-committed records (`LockId`, `CloseLockCommand`): the Eclipse
  formatter (`config/eclipse-google-style.xml`) does not understand Java records / compact constructors
  and mangles them onto one line. This means `./mvnw verify` is currently red regardless of #42.
  Fix options: switch Spotless from the Eclipse XML to `googleJavaFormat()` (supports records) or bump the
  Eclipse formatter. Needs a decision before the DoD (`./mvnw verify`) can pass.

## Testing strategy
- Use case (domain): unit test port-to-port — DONE (`CloseLockHandlerTest` mocks `LockPort`).
- Primary adapter (channel → bus): integration test asserting the correct `CloseLockCommand` is published to the bus. — NOT STARTED (no controller/channel yet).
- Secondary adapter (`TedeeApiAdapter`): integration test from the port method asserting the correct REST request (verb, path, `api_token` header). — CURRENT FOCUS.

## Steps
- ~~`LockId` value object with guard clause~~
- ~~`InvalidLockIdException`~~
- ~~`LockPort` output port~~
- ~~`CloseLockCommand` (primitive `int deviceId`)~~
- ~~`CloseLockHandler` delegates to `LockPort`~~
- ~~`TedeeApiAdapter` stub registered as `@Component`~~
- ~~`TedeeAutomationApplication` moved to root package `org.sanmibuh.tedee`~~
- ~~Download OpenAPI spec into repo (`openapi/tedee-bridge-api.json`)~~
- ~~Add `openapi-generator-maven-plugin` (java/restclient, 7.25.0) generating the Tedee client; compiles cleanly~~
- ~~`TedeeApiAdapter` — delegate to generated `LockApi`: `POST /v1.0/lock/{deviceId}/lock` with header `api_token`, expects 204~~
- ~~`TedeeProperties` — `@ConfigurationProperties(prefix = "sanmibuh.rest.tedee")` with `apiKey` + base URL~~
- ~~Spring `@Configuration` wiring the `ApiClient`/`LockApi` bean with base URL + `api_token` api key~~
- ~~Configure `application.yml` with `sanmibuh.rest.tedee.api-key` and base URL~~
- ~~Fix Spotless/records blocker so `./mvnw verify` passes (migrated to `googleJavaFormat`)~~
- ~~Map bridge error statuses to domain exceptions (401/404/405/406 + generic fallback), via `toDomainException`~~
- ~~Update `ARCHITECTURE.md` (tedee.lock bounded context + adapter + domain exceptions)~~
- GraalVM native reflection hints for generated client — **DEFERRED** (see note below)

## GraalVM hints — deferred (decision)
The `lock` flow never (de)serializes the generated Jackson models: success is `204` (no body) and
errors are handled by status code only. Registering reflection hints now would be dead config (YAGNI).
Add a `RuntimeHintsRegistrar` (or `reflect-config.json`) for `com.tedee.bridge.client.model.*` as soon as
response bodies start being parsed. Documented in `ARCHITECTURE.md` ("GraalVM note").

## Remaining for #42
- Primary adapter (channel/controller → bus): integration test asserting the correct `CloseLockCommand`
  is published to the bus. NOT STARTED (no controller/channel yet).

## Next step
🔴 RED — primary adapter: incoming request/channel publishes a `CloseLockCommand(deviceId)` to the
`CommandBus`. Integration test verifying the published command.


## Open decisions
- Spotless/records blocker fix: `googleJavaFormat()` vs bump Eclipse formatter (see "Known blocker").
- GraalVM hints: `@RegisterReflectionForBinding` for generated models vs `reflect-config.json`.
