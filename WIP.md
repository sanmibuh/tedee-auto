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
- The Tedee API client is **generated with openapi-generator** from the local spec (offline, no network needed to build).
- The generated client is a **collaborator of the secondary adapter** (`TedeeApiAdapter`), NOT the port itself.
- The domain secondary port (`LockPort`) stays decoupled from the generated model; the adapter maps between domain and generated client.
- Generated sources must be excluded from Spotless, NullAway and ErrorProne, and covered by GraalVM native reflection hints.

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
- Add `openapi-generator-maven-plugin` to generate the Tedee client (offline, from local spec)
- Exclude generated sources from Spotless / NullAway / ErrorProne
- `TedeeApiAdapter` — delegate to generated client: `POST /v1.0/lock/{deviceId}/lock` with header `api_token`
- `TedeeProperties` — `@ConfigurationProperties(prefix = "tedee")` with `apiKey` (+ base URL)
- Configure `application.properties` with `tedee.api-key` and base URL
- GraalVM native reflection hints for generated client
- Update `ARCHITECTURE.md`

## Next step
Add `openapi-generator-maven-plugin` and generate the client from `openapi/tedee-bridge-api.json`,
then wire it as a collaborator of `TedeeApiAdapter`. After the client exists, do 🔴 RED for the
adapter integration test asserting the correct REST request (`api_token` header, `/v1.0/lock/{id}/lock`, expects 204).

## Open decisions
- openapi-generator generator flavor: `java` with `library=restclient` (Spring Boot 4 idiomatic, aligns with hand-usage of `RestClient`) vs `spring` (`resttemplate`/`webclient`). Prefer `restclient`.
- HTTP mock in the adapter test: `MockRestServiceServer` bound to the `RestClient.Builder` used by the generated client (no extra deps).
