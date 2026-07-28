# Migration to Spring Boot 4.1 (Spring Framework 7, Jackson 3, JUnit 6, Testcontainers 2)

## Goal

All three modules (`jbst-foundation`, `jbst-server-iam`, `jbst-server-hardware-monitoring`) build and pass unit + integration tests (both MongoDB and PostgreSQL Testcontainers paths) on the latest Spring generation: Spring Boot 4.1.x (Spring Framework 7.0, Spring Security 7.0, Hibernate ORM 7.1, Jackson 3.0, JUnit Jupiter 6, Liquibase 5.0, Testcontainers 2.0, Tomcat 11 / Servlet 6.1). Every third-party integration that today pins a Spring-Boot-3-era version (jasypt, springdoc, hypersistence-utils, Feign+Jackson, jjwt, hibernate-jpamodelgen, jakarta.persistence) is upgraded or explicitly bridged, and both IAM database profiles plus the hardware-monitoring server start cleanly. Currently the repo is on Spring Boot 3.4.2 / Jackson 2.18.2 / JUnit 5.11.4 / Testcontainers 1.21.4.

## Assumptions

- "Latest Spring" = Spring Boot **4.1.x** (4.1.0 released 2026-06-10; use the newest 4.1 patch at execution time). If a blocking third-party incompatibility is hit (most likely jasypt), fall back to latest 4.0.x — both are the Spring Framework 7 / Jackson 3 generation. Sources: [Spring Boot 4.1.0 announcement](https://spring.io/blog/2026/06/10/spring-boot-4/), [Spring Boot 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes).
- Java stays at 21 (Boot 4 baseline is Java 17+; no JDK change needed).
- The migration is done in one release cycle (v1.73) but staged internally: first 3.4.2 → 3.5.x (deprecation sweep), then 3.5.x → 4.1.x. Only the final state is committed.
- Jackson 2 may remain on the runtime classpath **only** as a bridge for libraries that still require it (`feign-jackson`, `jjwt-jackson`) if their Jackson-3-native releases are not yet GA at execution time. Application/Spring serialization moves fully to Jackson 3 (`tools.jackson.*`).
- `jasypt-spring-boot-starter` 4.0.4 is the intended version; community reports mixed Boot 4 compatibility ([jasypt-spring-boot#417](https://github.com/ulisesbocchio/jasypt-spring-boot/issues/417), [releases](https://github.com/ulisesbocchio/jasypt-spring-boot/releases)) — the plan includes a verification step and two fallbacks. Encrypted values themselves (`PBEWithMD5AndDES`) do not change.
- Testcontainers 2.0 artifact renames apply (`junit-jupiter` → `testcontainers-junit-jupiter`, module artifacts get `testcontainers-` prefix) per [Testcontainers JUnit 5 docs](https://java.testcontainers.org/test_framework_integration/junit_5/).
- No `@MockBean`/`@SpyBean` usage exists in the repo (verified by grep), so the Boot 4 removal of those annotations requires no code change.

## Stack / constraints

- Spring Boot 4.1.x BOM as the single source of managed versions; keep the existing "matches version in spring-boot-dependencies" pinning convention in the parent POM but re-align every pin to the Boot 4.1 BOM values.
- Jackson 3 (`tools.jackson.*` packages, `tools.jackson.core:*` / `tools.jackson.databind` coordinates); annotations remain `com.fasterxml.jackson.annotation` (jackson-annotations 3 keeps the old package — the 66 files importing only annotations do not change).
- JUnit Jupiter 6 + Mockito (Boot-managed, ~5.20) + AssertJ (Boot-managed); Surefire/Failsafe split unchanged.
- Testcontainers 2.x for both MongoDB and PostgreSQL integration tests.
- Liquibase 5.0 for the Postgres schema (existing `postgres/changes/*.sql` changelogs must keep working).
- Lombok + delombok build pipeline, JaCoCo, Sonar, Maven Wrapper 3.9.9, Java 21 — all retained.
- Build must stay green via `./mvnw clean verify` with no new test skips.

## Affected files

```
pom.xml                                                    # all version properties, dependencyManagement, plugin mgmt
jbst-foundation/pom.xml                                    # starter/artifact renames (testcontainers, jackson bridge)
jbst-server-iam/pom.xml                                    # starter/artifact renames
jbst-server-hardware-monitoring/pom.xml                    # starter/artifact renames
CHANGELOG.md                                               # feat!/build! changelog lines under v1.73

# Security / WebSocket (Spring Security 7 removals)
jbst-foundation/src/main/java/jbst/foundation/configurations/JbstConfigurationSecurityJwt.java   # extends removed AbstractSecurityWebSocketMessageBrokerConfigurer — rewrite
jbst-foundation/src/main/java/jbst/foundation/configurations/JbstAbstractSecurityJwtConfigurer.java
jbst-foundation/src/main/java/jbst/foundation/configurations/JbstConfigurationSecurityJwtWebMVC.java
jbst-server-iam/src/main/java/jbst/server/iam/configurations/ConfigurationServer.java
jbst-server-hardware-monitoring/src/main/java/jbst/server/hm/configurations/JbstServerConfiguration.java

# Jackson 3 package migration (com.fasterxml.jackson.databind/core/datatype → tools.jackson.*)
#   57 Java files across all three modules import databind/core/datatype today, concentrated in:
jbst-foundation/src/main/java/jbst/foundation/domain/development/JbstDevelopment.java            # JsonProcessingException → JacksonException (unchecked)
jbst-foundation/src/main/java/jbst/foundation/utilities/** (JSON serialize/deserialize helpers)
jbst-foundation/src/main/java/jbst/foundation/domain/** (custom JsonSerializer/JsonDeserializer, ObjectMapper users)
jbst-foundation/src/main/java/jbst/foundation/feigns/** (Feign client decoders — bridge or migrate)
jbst-foundation/src/main/java/jbst/foundation/services/**, incidents/**, websockets/**
jbst-foundation/src/test/java/** (incl. AbstractTestRunnerResources.java, TuplePercentageTest.java,
                                  HtmlOptionsTest.java, JbstGeoUtilsFlagsConsoleTest.java)
jbst-foundation/src/test-integration/java/** (Mongo/Postgres repository ITs)
jbst-server-iam/src/main/java/**, src/test/java/** (incl. TestRunnerResources.java)
jbst-server-hardware-monitoring/src/main/java/**, src/test/java/**

# JPA / Hibernate 7 touchpoints
jbst-foundation + jbst-server-iam postgres entity classes using hypersistence-utils types (@Type/PostgreSQLTextArrayType etc.)
target/metamodel consumers (regenerated by hibernate-jpamodelgen 7 — no manual edit expected)

# New file
assets/plans/2026-07-28-spring-boot-4-migration.md         # this plan
```

(The 1 file importing `org.springframework.http.HttpHeaders` must be checked against the Framework 7 `HttpHeaders`-no-longer-`MultiValueMap` change; the 12 MockMvc test files must be checked against Framework 7 `spring-test` — expected to compile unchanged.)

## Ordered steps

1. **Baseline**: run `./mvnw clean verify` on the current branch and record the passing test counts (Surefire + Failsafe, Mongo + Postgres containers). This is the regression yardstick.
2. **Intermediate hop to Spring Boot 3.5.x** (throwaway commit-less stage): bump `version.dependency.springframework.boot` to the latest 3.5.x, add `spring-boot-properties-migrator` (runtime, temporary), build, and fix every deprecation warning that Framework 7 / Security 7 will turn into a removal — especially anything flagged in `JbstConfigurationSecurityJwt` and property renames reported by the migrator. Do not keep the migrator dependency.
3. **Parent POM — core generation bump**: set Boot to 4.1.x; re-align all "(matches version in spring-boot-dependencies)" pins to the Boot 4.1 BOM (verify each with `./mvnw dependency:tree -Dincludes=<ga>`): JUnit BOM → Jupiter 6.x, AssertJ, json-path, commons-codec, Lombok, Liquibase → 5.0.x, Hibernate → 7.1.x, `jakarta.persistence-api` 3.1.0 → BOM-managed 3.2.x, Testcontainers → 2.x. Prefer **deleting** redundant explicit versions and letting the BOM manage them where the pin adds nothing.
4. **Parent POM — third-party integrations**: `springdoc-openapi-starter-webmvc-ui` → 3.x (Boot-4 line); `jasypt-spring-boot-starter` → 4.0.4 (see step 12 verification + fallbacks); `hypersistence-utils-hibernate-63` → `hypersistence-utils-hibernate-71`; `hibernate-jpamodelgen` → 7.1.x (same version as Boot's Hibernate); jjwt → latest 0.1x release; Feign → latest 13.x/14.x. Check starter renames in Boot 4 (`spring-boot-starter-web` → `spring-boot-starter-webmvc` canonical name) and adopt the new names in all three module POMs.
5. **Testcontainers 2 renames**: in parent + foundation POMs change `org.testcontainers:junit-jupiter` → `testcontainers-junit-jupiter`, `mongodb` → `testcontainers-mongodb`, `postgresql` → `testcontainers-postgresql`; adjust any `@Testcontainers`/container API breakages in `src/test-integration/java`. Keep the `--add-opens java.base/java.time=ALL-UNNAMED` argLine (re-test whether Mongo ITs still need it; keep if unsure).
6. **Jackson 3 dependency wiring**: replace the `com.fasterxml.jackson:jackson-bom` 2.18.2 import with the Boot-managed Jackson 3 (`tools.jackson`); decide the Feign/jjwt bridge: if `feign-jackson` (and `jjwt-jackson`) Jackson-3-native variants are GA, use them; otherwise keep a minimal Jackson **2** runtime pair (`com.fasterxml.jackson.core:jackson-databind` 2.x via jackson-bom 2.x import) scoped only to satisfy `feign-jackson`/`jjwt-jackson`, with a POM comment explaining the bridge. Jackson 2 and 3 coexist safely (different groupIds + packages).
7. **Jackson 3 code migration (57 files)**: mechanical rewrite `com.fasterxml.jackson.databind.*` → `tools.jackson.databind.*`, `com.fasterxml.jackson.core.*` → `tools.jackson.core.*`, `com.fasterxml.jackson.datatype.*` → built-in (Java-time support is built into Jackson 3 — delete `JavaTimeModule` registrations). Semantic fixes: `JsonProcessingException` → `tools.jackson.core.JacksonException` (now unchecked — remove `throws` clauses in `JbstDevelopment`, `AbstractTestRunnerResources`, `TestRunnerResources`, and the 4 test classes); `new ObjectMapper()` + configure → `JsonMapper.builder()...build()` (mappers are immutable in Jackson 3); custom `JsonSerializer`/`JsonDeserializer` subclasses → new `ValueSerializer`/`ValueDeserializer` signatures (`JsonGenerator`/`SerializerProvider` package + API changes). Do **not** touch the 66 files importing only `com.fasterxml.jackson.annotation.*`.
8. **Spring Security 7 — WebSocket STOMP security rewrite**: `JbstConfigurationSecurityJwt` extends `AbstractSecurityWebSocketMessageBrokerConfigurer`, which is removed in Security 7. Rewrite to the authorization-manager model: `@EnableWebSocketSecurity` **or** (to preserve the current no-CSRF behavior documented in the class's own comment at line 51) manual wiring of `AuthorizationChannelInterceptor` with a `MessageMatcherDelegatingAuthorizationManager` reproducing today's inbound message rules, registered via a `WebSocketMessageBrokerConfigurer`. Preserve the JWT handshake/`filters/jwt` + `handshakes/` integration exactly; mirror rules from `jbst.security.websockets` properties.
9. **Spring Security 7 / Framework 7 — HTTP layer sweep**: in `JbstConfigurationSecurityJwt`, `JbstAbstractSecurityJwtConfigurer`, server `ConfigurationServer`/`JbstServerConfiguration`: ensure pure lambda DSL (no `and()`), replace any `AntPathRequestMatcher` with `PathPatternRequestMatcher`, adapt the single `org.springframework.http.HttpHeaders` consumer if it uses the removed `MultiValueMap` inheritance, and replace any `MappingJackson2HttpMessageConverter`/`MappingJackson2MessageConverter` references with `JacksonJsonHttpMessageConverter`/`JacksonJsonMessageConverter` equivalents (Boot auto-config should cover the defaults).
10. **Hibernate 7 / JPA 3.2 / Liquibase 5 (Postgres path)**: recompile entity + metamodel generation with jpamodelgen 7; fix hypersistence-utils type API changes on Postgres entities; run the full Liquibase 5 changelog (`postgres/changelog.yml` `includeAll`) against a fresh Testcontainers Postgres via the existing ITs; verify Mongo path (Spring Data MongoDB in Boot 4, health-indicator/property renames from the release notes) — update `application*.yml` keys if the properties migrator flagged any.
11. **Test stack on JUnit 6**: build test sources; fix Jupiter 6 breakages (removed deprecated APIs; `junit-jupiter-engine`/`params` coordinates stay `org.junit.jupiter`); verify Surefire/Failsafe 3.5.2 runs Jupiter 6 (bump plugins to latest 3.x if discovery fails); confirm the 12 MockMvc test classes compile against Framework 7 `spring-test` and optionally note (not adopt) `MockMvcTester`/`RestTestClient`.
12. **Jasypt verification**: with `jasypt-spring-boot-starter` 4.0.4, run a focused context-load test that decrypts a `PBEWithMD5AndDES` value under Boot 4.1. If it fails (known issue #417): fallback A — pin 3.0.5 (reported working on Boot 4); fallback B — replace the starter with a small custom `org.springframework.boot.EnvironmentPostProcessor` that wraps `PropertySource`s with a Jasypt `StringEncryptor` (same algorithm, same `jasypt.encryptor.password` contract) so `run-mongodb.sh`/`run-postgres.sh`/`run.sh` keep working unchanged.
13. **Build plumbing**: confirm delombok (lombok-maven-plugin with forced project Lombok), JaCoCo 0.8.x, git-commit-id, springdoc + actuator `/api/actuator/info` all still work under Boot 4; verify `spring-boot-maven-plugin` `repackage`/`executable` config unchanged; update `CHANGELOG.md` v1.73 with `feat!:`/`build!:` lines describing the breaking framework migration.
14. **Full verification + server smoke** (see Verification): full matrix, then boot both IAM profiles and hardware-monitoring locally against dockerized DBs; check Swagger UI and actuator endpoints respond.

## Risks

- **jasypt-spring-boot may not fully support Boot 4.1** (constructor/package changes broke 4.0.3 on Boot 4). Mitigation: step 12 tests decryption in isolation first and defines two working fallbacks (pin 3.0.5, or a ~50-line custom `EnvironmentPostProcessor`) before the rest of the migration depends on it.
- **The WebSocket security rewrite is a behavioral change, not a rename** — `@EnableWebSocketSecurity` enforces CSRF on STOMP frames, which the current code deliberately avoids (comment in `JbstConfigurationSecurityJwt:51`). Mitigation: use the manual `AuthorizationChannelInterceptor` wiring to reproduce today's exact inbound rules and cover it with the existing websocket tests plus a manual STOMP connect smoke test.
- **Jackson 3's package move can silently split serialization** if any library keeps writing with Jackson 2 (Feign responses, jjwt claims) while Spring writes with Jackson 3 — different modules, different configuration. Mitigation: keep the Jackson 2 bridge explicitly scoped to Feign/jjwt internals, assert API response shapes in existing controller tests, and grep the final tree for accidental `com.fasterxml.jackson.databind` usage in application code.
- **Testcontainers 2.0 + Liquibase 5.0 are major bumps riding along** and can fail independently of Spring. Mitigation: they are isolated in dedicated steps (5, 10) with the Failsafe ITs as the gate; each can be temporarily pinned back (Testcontainers 1.21.x works on JUnit 6? — if not, fix forward; Liquibase 4.31.x is Boot-4-compatible fallback) without blocking the Spring bump.
- **Delombok toolchain lag**: lombok-maven-plugin 1.18.20.0 with a forced newer Lombok must still delombok sources that now reference `tools.jackson` types. Mitigation: bump Lombok to the Boot-4-managed version; delombok only parses Java syntax (not classpath types), so risk is low — trust final `BUILD SUCCESS` over delombok-phase noise per project convention.

## Verification

```bash
# 1. Full build: compile, delombok, unit tests (Surefire), integration tests (Failsafe, Mongo+Postgres Testcontainers), repackage
./mvnw clean verify

# 2. Confirm the effective dependency generation (no stray Jackson 2 in app scope, JUnit 6, Testcontainers 2)
./mvnw dependency:tree -Dincludes=tools.jackson.core,com.fasterxml.jackson.core,org.junit.jupiter,org.testcontainers,org.springframework:spring-core,org.springframework.security:spring-security-core

# 3. Unit-only and IT-only entry points still work
./execute-unit-tests-only.sh
./execute-integrations-tests-only.sh

# 4. Fast library install path
./build-fast.sh

# 5. Server smoke (manual, requires helper script + Jasypt password on PATH/env):
docker/run-postgres.sh && run-postgres.sh    # IAM on postgres profile
docker/run-mongo.sh    && run-mongodb.sh     # IAM on mongodb profile
run.sh                                       # hardware-monitoring
# then: curl -s http://localhost:3002/api/actuator/info  and open
# http://localhost:3002/api/swagger-ui/index.html — both must respond; STOMP websocket connect from dev client succeeds
```

Done when: `./mvnw clean verify` is green on Spring Boot 4.1.x with Jackson 3 / JUnit 6 / Testcontainers 2 (test counts ≥ the step-1 baseline), and both IAM profiles plus hardware-monitoring start locally with decrypted Jasypt config, serving actuator, Swagger UI, and STOMP websockets.
