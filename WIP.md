# WIP — Issue #42: Close Lock Use Case

## Steps
- ~~`LockId` value object with guard clause~~
- ~~`InvalidLockIdException`~~
- ~~`LockPort` output port~~
- ~~`CloseLockCommand` (primitive `int deviceId`)~~
- ~~`CloseLockHandler` delegates to `LockPort`~~
- ~~`TedeeApiAdapter` stub registered as `@Component`~~
- ~~`TedeeAutomationApplication` moved to root package `org.sanmibuh.tedee`~~
- `TedeeProperties` — `@ConfigurationProperties(prefix = "tedee")` with `apiKey`
- `TedeeApiAdapter` — implement `POST /lock/{deviceId}/lock` with `Authorization: PersonalKey <api-key>`
- Configure `application.yml` with `tedee.api-key`
- Update `ARCHITECTURE.md`

## Next step
🔴 RED — test for `TedeeApiAdapter`: calling `POST /lock/{deviceId}/lock` with `Authorization: PersonalKey <api-key>`.

## Open decisions
- How to mock HTTP in the adapter test: `MockRestServiceServer` (if using `RestClient`) or WireMock.
