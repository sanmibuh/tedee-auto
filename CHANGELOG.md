# Changelog

## [0.2.2] - 2026-09-21

### Other
- Native image: TedeeProperties JSR-303 @AssertTrue methods missing reflection hints + no pre-publish native validation ([#228](https://github.com/sanmibuh/tedee-auto/pull/228))

## [0.2.1] - 2026-09-21

### Other
- Native image: container exits at startup with MissingReflectionRegistrationError on TedeeProperties$Retry.multiplier ([#224](https://github.com/sanmibuh/tedee-auto/pull/224))

## [0.2.0] - 2026-09-21

### Features
- feat: configure retry policy for transient Tedee bridge errors ([#214](https://github.com/sanmibuh/tedee-auto/pull/214))

### Bug Fixes
- Docs: fix deployment config mount path (workdir is /home/nonroot, not /) ([#207](https://github.com/sanmibuh/tedee-auto/pull/207))

### Code Quality
- [Refactor] Make Lock write-side event-driven: pure save + LockLocked event handler ([#211](https://github.com/sanmibuh/tedee-auto/pull/211))

### Dependencies
- build(deps): bump actions/upload-pages-artifact from 4 to 5 ([#209](https://github.com/sanmibuh/tedee-auto/pull/209))
- build(deps): bump actions/deploy-pages from 4 to 5 ([#210](https://github.com/sanmibuh/tedee-auto/pull/210))

### Build & CI
- CI: publish Docker build fails with 'failed to reserve cache' (drop type=gha cache) ([#220](https://github.com/sanmibuh/tedee-auto/pull/220))
- CI: reduce GHA cache usage (publish Docker + Qodana) ([#217](https://github.com/sanmibuh/tedee-auto/pull/217))
- ci(publish): trigger the publish workflow only on release PR merges ([#208](https://github.com/sanmibuh/tedee-auto/pull/208))

### Other
- Make timezone human-friendly for scheduler and logs (keep JVM in UTC) ([#213](https://github.com/sanmibuh/tedee-auto/pull/213))

## [0.1.5] - 2026-09-19

### Other
- Native image aborts with "CPU ISA level is lower than required" on CPUs without AVX2 (x86-64-v2) ([#203](https://github.com/sanmibuh/tedee-auto/pull/203))
- Harden release/publish workflow: order-independent changelog, fail-fast publish, single-release guard ([#201](https://github.com/sanmibuh/tedee-auto/pull/201))

## [0.1.4] - 2026-09-18

### Other
- Native image still fails with 'CPU ISA level is lower than required' on QNAP TS-464 (Celeron N5095) ([#198](https://github.com/sanmibuh/tedee-auto/pull/198))
- Publish JaCoCo and PITest HTML reports to GitHub Pages ([#194](https://github.com/sanmibuh/tedee-auto/pull/194))

## [0.1.3] - 2026-09-18

### Other
- Native image fails on CPUs without AVX2 (CPU ISA level is lower than required) ([#192](https://github.com/sanmibuh/tedee-auto/pull/192))
- Clean up release automation: changelog noise, PR naming, and README version pinning ([#189](https://github.com/sanmibuh/tedee-auto/pull/189))

## [0.1.2] - 2026-09-18

### Other
- Docs: add Docker Compose deployment guide and link package/releases ([#183](https://github.com/sanmibuh/tedee-auto/pull/183))
- Native image fails to start: libz.so.1 missing in distroless runtime ([#185](https://github.com/sanmibuh/tedee-auto/pull/185))

## [0.1.1] - 2026-09-18

### Bug Fixes
- fix(docker): copy the OpenAPI spec into the image so the native build doesn't fail ([#177](https://github.com/sanmibuh/tedee-auto/pull/177))

## [0.1.0] - 2026-09-18

### Features
- [Feature] Introduce EventBus and domain-event publishing pipeline ([#131](https://github.com/sanmibuh/tedee-auto/pull/131))
- feat: primary adapter to publish CloseLockCommand ([#107](https://github.com/sanmibuh/tedee-auto/pull/107))
- feat: close lock use case — Tedee aggregate ([#98](https://github.com/sanmibuh/tedee-auto/pull/98))
- feat(nightly): persist model usage statistics in orphan branch ([#44](https://github.com/sanmibuh/tedee-auto/pull/44))
- feat: define a manual release workflow ([#27](https://github.com/sanmibuh/tedee-auto/pull/27))
- feat: configure Dependabot for Maven and auto-merge patch/minor updates ([#16](https://github.com/sanmibuh/tedee-auto/pull/16))

### Bug Fixes
- Fix TypeParameterUnusedInFormals warning in HandlerLookup.find ([#112](https://github.com/sanmibuh/tedee-auto/pull/112))
- fix(nightly): avoid re-reporting closed ai-review issues ([#89](https://github.com/sanmibuh/tedee-auto/pull/89))
- fix(nightly): handle truncated Gemini responses due to maxOutputTokens limit ([#39](https://github.com/sanmibuh/tedee-auto/pull/39))
- fix(nightly): improve Gemini fallback chain error handling and model label lifecycle ([#36](https://github.com/sanmibuh/tedee-auto/pull/36))
- fix(nightly): update Gemini model list to available and recommended models ([#34](https://github.com/sanmibuh/tedee-auto/pull/34))

### Code Quality
- [SOLID] Missing @ConditionalOnMissingBean on registrars in DddAutoConfiguration ([#167](https://github.com/sanmibuh/tedee-auto/pull/167))
- [TDD] Missing BDDSoftAssertions in InMemoryEventBusTest for multi-assertion test ([#162](https://github.com/sanmibuh/tedee-auto/pull/162))
- [TDD] Rigid test setup in LockSchedulingConfigurerTest conflates Arrange and Act phases ([#158](https://github.com/sanmibuh/tedee-auto/pull/158))
- [SOLID] Missing ArchUnit rule to enforce that commands and queries do not depend on domain types ([#157](https://github.com/sanmibuh/tedee-auto/pull/157))
- [Clean Code] Visibility mismatch in CQRSAutoConfiguration bean definition ([#151](https://github.com/sanmibuh/tedee-auto/pull/151))
- [Clean Code] Missing validation on TedeeProperties prevents failing fast at startup ([#150](https://github.com/sanmibuh/tedee-auto/pull/150))
- [Clean Code] Boxed Integer and missing null check in LockId record ([#149](https://github.com/sanmibuh/tedee-auto/pull/149))
- [TDD] Inconsistent exception name in TedeeLockRepositoryTest method ([#148](https://github.com/sanmibuh/tedee-auto/pull/148))
- [Clean Code] Misleading method name toDomainException in TedeeLockRepository ([#147](https://github.com/sanmibuh/tedee-auto/pull/147))
- [TDD] Subject under test (SUT) not named 'sut' in multiple test classes ([#146](https://github.com/sanmibuh/tedee-auto/pull/146))
- [TDD] Non-compliant test method names in TedeeClientConfigurationTest ([#145](https://github.com/sanmibuh/tedee-auto/pull/145))
- [Clean Code] Redundant @SuppressWarnings("unused") on Query interface ([#144](https://github.com/sanmibuh/tedee-auto/pull/144))
- [Clean Code] TedeeAutomationApplication main method should be public ([#143](https://github.com/sanmibuh/tedee-auto/pull/143))
- [Clean Code] Redundant and confusing helper method findBeanClassNamePITEquivalent ([#142](https://github.com/sanmibuh/tedee-auto/pull/142))
- [Clean Code] Non-use of var for local variable in HandlerLookup constructor ([#141](https://github.com/sanmibuh/tedee-auto/pull/141))
- [Refactor] Rename Simple*Bus to InMemory*Bus for consistency and future implementations ([#130](https://github.com/sanmibuh/tedee-auto/pull/130))
- [TDD] ArchUnit classes imported per test method instead of using static cache or @AnalyzeClasses ([#88](https://github.com/sanmibuh/tedee-auto/pull/88))
- [Clean Code] HandlerLookup constructor uses complex Collectors.toMap with redundant type resolution ([#87](https://github.com/sanmibuh/tedee-auto/pull/87))
- [Clean Code] HandlerLookup uses h.getClass() directly which fails for proxied handlers ([#85](https://github.com/sanmibuh/tedee-auto/pull/85))
- [Clean Code] GlobalExceptionHandler should handle technical exceptions consistently ([#84](https://github.com/sanmibuh/tedee-auto/pull/84))
- [SOLID] CQRSAutoConfiguration hardcodes package scan to org.sanmibuh ([#80](https://github.com/sanmibuh/tedee-auto/pull/80))
- [SOLID] CQRSAutoConfiguration lacks conditional registration for CommandBus and QueryBus ([#77](https://github.com/sanmibuh/tedee-auto/pull/77))
- [Clean Code] Use of explicit type instead of var in HexagonalArchitectureTest ([#74](https://github.com/sanmibuh/tedee-auto/pull/74))
- [TDD] Non-compliant test method name in GlobalExceptionHandlerTest ([#72](https://github.com/sanmibuh/tedee-auto/pull/72))
- [SOLID] HandlerNotFoundException should not extend DomainException ([#71](https://github.com/sanmibuh/tedee-auto/pull/71))
- [TDD] Test method naming violates `should_doSomething_whenCondition` convention ([#47](https://github.com/sanmibuh/tedee-auto/pull/47))
- [Clean Code] Missing `final` on parameter and non-use of `var` for local variables ([#45](https://github.com/sanmibuh/tedee-auto/pull/45))

### Dependencies
- build(deps): bump advanced-security/maven-dependency-submission-action from 5 to 6 ([#161](https://github.com/sanmibuh/tedee-auto/pull/161))
- build(deps-dev): bump com.diffplug.spotless:spotless-maven-plugin from 3.10.1 to 3.10.2 ([#160](https://github.com/sanmibuh/tedee-auto/pull/160))
- build(deps-dev): bump io.github.hakky54:logcaptor from 2.11.0 to 2.12.7 ([#154](https://github.com/sanmibuh/tedee-auto/pull/154))
- build(deps-dev): bump org.pitest:pitest-maven from 1.25.9 to 1.30.0 ([#153](https://github.com/sanmibuh/tedee-auto/pull/153))
- build(deps): bump com.uber.nullaway:nullaway from 0.14.0 to 0.14.1 ([#155](https://github.com/sanmibuh/tedee-auto/pull/155))
- build(deps-dev): bump com.diffplug.spotless:spotless-maven-plugin from 3.10.0 to 3.10.1 ([#152](https://github.com/sanmibuh/tedee-auto/pull/152))
- build(deps): bump actions/checkout from 4 to 7 ([#117](https://github.com/sanmibuh/tedee-auto/pull/117))
- build(deps): bump JetBrains/qodana-action from 2026.1 to 2026.2 ([#118](https://github.com/sanmibuh/tedee-auto/pull/118))
- build(deps): bump actions/setup-java from 4 to 6 ([#116](https://github.com/sanmibuh/tedee-auto/pull/116))
- build(deps): bump com.uber.nullaway:nullaway from 0.13.7 to 0.14.0 ([#115](https://github.com/sanmibuh/tedee-auto/pull/115))
- build(deps): bump org.openapitools:jackson-databind-nullable from 0.2.6 to 0.2.11 ([#114](https://github.com/sanmibuh/tedee-auto/pull/114))
- build(deps): bump org.springframework.boot:spring-boot-starter-parent from 4.1.0 to 4.1.1 in the spring-boot group ([#113](https://github.com/sanmibuh/tedee-auto/pull/113))
- build(deps): bump org.apache.maven:apache-maven from 3.9.11 to 3.9.16 ([#68](https://github.com/sanmibuh/tedee-auto/pull/68))
- build(deps-dev): bump com.diffplug.spotless:spotless-maven-plugin from 3.9.0 to 3.10.0 ([#67](https://github.com/sanmibuh/tedee-auto/pull/67))
- build(deps-dev): bump com.diffplug.spotless:spotless-maven-plugin from 2.44.5 to 3.9.0 ([#22](https://github.com/sanmibuh/tedee-auto/pull/22))
- build(deps): bump docker/build-push-action from 6 to 7 ([#28](https://github.com/sanmibuh/tedee-auto/pull/28))
- build(deps): bump docker/setup-buildx-action from 3 to 4 ([#31](https://github.com/sanmibuh/tedee-auto/pull/31))
- build(deps): bump docker/login-action from 3 to 4 ([#30](https://github.com/sanmibuh/tedee-auto/pull/30))
- build(deps): bump actions/upload-artifact from 4 to 7 ([#26](https://github.com/sanmibuh/tedee-auto/pull/26))
- build(deps): bump actions/checkout from 4 to 7 ([#25](https://github.com/sanmibuh/tedee-auto/pull/25))
- build(deps): bump actions/cache from 4 to 6 ([#23](https://github.com/sanmibuh/tedee-auto/pull/23))
- build(deps): bump actions/download-artifact from 4 to 8 ([#20](https://github.com/sanmibuh/tedee-auto/pull/20))
- build(deps): bump actions/github-script from 7 to 9 ([#18](https://github.com/sanmibuh/tedee-auto/pull/18))
- build(deps): bump dependabot/fetch-metadata from 2 to 3 ([#21](https://github.com/sanmibuh/tedee-auto/pull/21))
- build(deps): bump advanced-security/maven-dependency-submission-action from 4 to 5 ([#17](https://github.com/sanmibuh/tedee-auto/pull/17))
- build(deps-dev): bump com.tngtech.archunit:archunit-junit5 from 1.4.1 to 1.5.0 ([#24](https://github.com/sanmibuh/tedee-auto/pull/24))
- build(deps-dev): bump org.jacoco:jacoco-maven-plugin from 0.8.13 to 0.8.15 ([#19](https://github.com/sanmibuh/tedee-auto/pull/19))

### Build & CI
- chore: add GraalVM reflection hints for generated Tedee client models ([#122](https://github.com/sanmibuh/tedee-auto/pull/122))
- chore: ArchUnit rules for primary/secondary hexagonal slices ([#119](https://github.com/sanmibuh/tedee-auto/pull/119))

### Other
- Tedee Bridge auth: implement Encrypted api_token generation ([#174](https://github.com/sanmibuh/tedee-auto/pull/174))
- App fails to start: RestClient.Builder bean missing at runtime (Spring Boot 4) ([#172](https://github.com/sanmibuh/tedee-auto/pull/172))
- Add missing `final` modifier on try-with-resources variable in test ([#165](https://github.com/sanmibuh/tedee-auto/pull/165))
- Use var for local variable in EventHandlerRegistry constructor ([#164](https://github.com/sanmibuh/tedee-auto/pull/164))
- Add missing `final` modifier to local variables in TedeeBridgeRuntimeHints ([#163](https://github.com/sanmibuh/tedee-auto/pull/163))
- EventBus silently drops domain events with no registered handler ([#140](https://github.com/sanmibuh/tedee-auto/pull/140))
- Silence native access warnings (jansi) from the Maven wrapper ([#126](https://github.com/sanmibuh/tedee-auto/pull/126))
- Exclude generated code (openapi-generator) from ErrorProne checks ([#111](https://github.com/sanmibuh/tedee-auto/pull/111))
- Improve org.sanmibuh.ddd.domain base abstractions ([#102](https://github.com/sanmibuh/tedee-auto/pull/102))
- Improve local PITest incremental execution using coverage-data history ([#101](https://github.com/sanmibuh/tedee-auto/pull/101))
- Install NullAway with Error Prone ([#86](https://github.com/sanmibuh/tedee-auto/pull/86))
- [Qodana] Improve qodana.yaml and workflow configuration for Community linter ([#79](https://github.com/sanmibuh/tedee-auto/pull/79))
- Add qodana CI checks ([#76](https://github.com/sanmibuh/tedee-auto/pull/76))
- [Docs] Missing ArchUnit rule for cross-aggregate coupling in HexagonalArchitectureTest ([#75](https://github.com/sanmibuh/tedee-auto/pull/75))
- Add qodana CI checks ([#73](https://github.com/sanmibuh/tedee-auto/pull/73))
- Add Qodana static analysis ([#70](https://github.com/sanmibuh/tedee-auto/pull/70))
- Improve OpenCode harness configuration ([#54](https://github.com/sanmibuh/tedee-auto/pull/54))
- Update Maven Wrapper to 3.9.11 ([#52](https://github.com/sanmibuh/tedee-auto/pull/52))
- Add clean target to Makefile ([#50](https://github.com/sanmibuh/tedee-auto/pull/50))
- Add make format target and strengthen automatic code formatting ([#48](https://github.com/sanmibuh/tedee-auto/pull/48))
- Nightly AI Review #5 failed: Run Gemini review exited with code 1 ([#32](https://github.com/sanmibuh/tedee-auto/pull/32))
- Improve CI quality report ([#13](https://github.com/sanmibuh/tedee-auto/pull/13))
- Add OpenCode harness: git safety guardrails and TDD skill ([#11](https://github.com/sanmibuh/tedee-auto/pull/11))
- [ci/gemini]: feat: enhance nightly AI review workflow with issue fetc… ([#9](https://github.com/sanmibuh/tedee-auto/pull/9))
- [ci/nightly]: feat: add nightly AI review workflow for automated code… ([#8](https://github.com/sanmibuh/tedee-auto/pull/8))
- [fix/ci-pit]: fix: improve CI workflow for PITest history management … ([#7](https://github.com/sanmibuh/tedee-auto/pull/7))
- [fix/ci-main]: fix: improve CI workflow for pushing coverage metrics … ([#6](https://github.com/sanmibuh/tedee-auto/pull/6))
- [ci/coverage-lines]: fix: update worktree commands and enhance covera… ([#5](https://github.com/sanmibuh/tedee-auto/pull/5))
- [fix/ci]: fix: update CI workflow to create and push coverage-data br… ([#4](https://github.com/sanmibuh/tedee-auto/pull/4))
- Add Dependabot configuration file ([#3](https://github.com/sanmibuh/tedee-auto/pull/3))
