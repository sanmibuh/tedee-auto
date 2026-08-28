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
- The Tedee API client is **generated with openapi-generator** (`7.14.0`) from the local spec (offline, no network needed to build).
- Generator flavor: `java` + **`library=native`** (JDK `java.net.http.HttpClient`).
  - `restclient`/`resttemplate`/`webclient` libraries are INCOMPATIBLE with Spring Framework 7 / Boot 4:
    Spring 7 `HttpHeaders` no longer implements `Map`, so the generated `ApiClient` (`headers.entrySet()`/`containsKey()`) fails to compile.
  - `native` avoids Spring HTTP types entirely → compiles cleanly.
- Extra deps required by the generated client: `jackson-datatype-jsr310`, `org.openapitools:jackson-databind-nullable`.
- Generated code lives in package `com.tedee.bridge.client.*` (outside `org.sanmibuh`) so NullAway/ErrorProne treat it as unannotated third-party code. Output: `target/generated-sources/openapi` (not under `src/`, so Spotless ignores it).
- The generated client is a **collaborator of the secondary adapter** (`TedeeApiAdapter`), NOT the port itself.
- The domain secondary port (`LockPort`) stays decoupled from the generated model; the adapter maps between domain and generated client.
- Auth: the `native` client does NOT auto-apply the `api_token` security header; it exposes a request interceptor
  (`Consumer<HttpRequest.Builder>`). The adapter sets it to add header `api_token: <apiKey>`.
- `LockApi.postLock(Integer deviceId)` → `POST {baseUri}/lock/{deviceId}/lock`; default baseUri `.../v1.0`.
- Still TODO: GraalVM native reflection hints for the generated Jackson models.

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
- ~~Add `openapi-generator-maven-plugin` (java/native) generating the Tedee client; compiles cleanly~~
- `TedeeApiAdapter` — delegate to generated `LockApi`: `POST /v1.0/lock/{deviceId}/lock` with header `api_token`, expects 204
- `TedeeProperties` — `@ConfigurationProperties(prefix = "tedee")` with `apiKey` + base URL
- Spring `@Configuration` wiring the `ApiClient`/`LockApi` bean with base URL + `api_token` interceptor
- Configure `application.properties` with `tedee.api-key` and base URL
- GraalVM native reflection hints for generated client
- Update `ARCHITECTURE.md`

## Next step
🔴 RED — integration test for `TedeeApiAdapter`: given a `LockId`, calling `lock(...)` performs
`POST /v1.0/lock/{deviceId}/lock` with header `api_token: <apiKey>`. Use **WireMock** as the local
HTTP server (JDK HttpClient → `MockRestServiceServer` is NOT applicable).

## Open decisions
- WireMock dependency to add: `org.wiremock:wiremock-standalone` (test scope). Confirm coordinates/version.
- GraalVM hints: `@RegisterReflectionForBinding` for generated models vs `reflect-config.json`.
