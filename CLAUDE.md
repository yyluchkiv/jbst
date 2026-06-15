# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

JBST is a Java 17 Spring Boot framework providing bootstrapping tools for enterprise applications. It is a multi-module Maven project:

- **jbst-foundation**: The shared library (JAR). Contains all domain objects, utilities, configurations, foundation services, the JWT-based IAM/security layer, the incidents subsystem, and Feign clients for external integrations. Both servers depend on it.
- **jbst-server-iam**: Identity & Access Management server. Runs against **either** MongoDB **or** PostgreSQL, selected by Spring profile. Port `3002`.
- **jbst-server-hardware-monitoring**: Hardware monitoring server. Port `6001`.

`jbst-foundation` is published/installed first; the servers consume it as a dependency. When iterating only on the library, `./build-fast.sh` installs just that module (`mvn -pl jbst-foundation -am clean install -DskipTests`).

## Build and Development Commands

```bash
# Compile only (no tests)
./compile-all.sh                  # mvn clean compile test-compile

# Unit tests only (Surefire)
./execute-unit-tests-only.sh      # mvn clean test

# Integration tests only (Failsafe)
./execute-integrations-tests-only.sh   # mvn failsafe:integration-test

# All tests (unit + integration)
mvn integration-test

# Full verification (what CI effectively runs)
mvn clean verify   # or `mvn clean install`

# Fast install, skip all tests
./delivery-check-fast.sh          # mvn clean install -Dmaven.test.skip -DskipTests -T 4

# Library-only fast install
./build-fast.sh                   # mvn -pl jbst-foundation -am clean install -DskipTests -T 4
```

### Running a single test
```bash
# Single unit test (class or method)
mvn test -Dtest=ClassName
mvn test -Dtest=ClassName#methodName

# Single integration test (Failsafe; *IT classes under src/test-integration/java)
mvn failsafe:integration-test -Dit.test=ClassNameIT#methodName
```

### Delombok build behavior (important)
The build runs the `lombok-maven-plugin` **delombok** goal before compilation, emitting expanded sources to `target/delombok` (used as the compile sourcepath, also feeding JaCoCo/Sonar). The delombok phase frequently prints alarming-looking errors/warnings even on a healthy build — **trust the final `BUILD SUCCESS`**, not delombok-phase noise.

## Testing Strategy

- **Unit tests**: `src/test/java`, run by Maven **Surefire**. Fast, mock-based.
- **Integration tests**: `src/test-integration/java` (a non-standard source root wired in via `build-helper-maven-plugin`'s `add-test-source`), run by Maven **Failsafe**. Only `jbst-foundation` currently has this source root.
- Integration tests use **Testcontainers** for both MongoDB and PostgreSQL to guarantee both database backends stay compatible.
- A global `argLine` (`--add-opens java.base/java.time=ALL-UNNAMED`) is set in the parent POM — required for the Mongo integration tests. Replicate it if you run tests outside Maven.

## Architecture

### Configuration is centralized in `JbstProperties`
All framework configuration flows through a single `@ConfigurationProperties(prefix = "jbst")` class:
`jbst-foundation/src/main/java/jbst/foundation/domain/properties/JbstProperties.java`. Anything under the `jbst:` YAML prefix (async/event thread pools, MVC/CORS, the entire JWT + WebSocket security stack, user-email and user-token flows) is bound here. When adding a configurable feature, extend `JbstProperties` rather than reading properties ad hoc.

Notably, **granted authorities are defined in YAML** (`jbst.security.authorities`) — both the enum `package-name` they map to and the list of `values`. See `jbst-server-iam/src/main/resources/application.yml` for the canonical example.

### IAM server: dual-database design
`jbst-server-iam` ships parallel `mongo` and `postgres` implementations under `jbst.server.iam.mongo.*` and `jbst.server.iam.postgres.*`, selected at runtime by Spring profile:
- **MongoDB** profile `mongodb` → `run-mongodb.sh`, configs `application-mongo[-dev].yml`
- **PostgreSQL** profile `postgres` → `run-postgres.sh`, configs `application-postgres[-dev].yml`

PostgreSQL schema is managed by **Liquibase**: `src/main/resources/postgres/changelog.yml` `includeAll`s `postgres/changes/*.sql`. MongoDB has no migrations. When changing the data model, update **both** the Mongo and Postgres paths — foundation mirrors them too (`repositories/mongo` + `repositories/postgres`, `services/mongo` + `services/postgres`, `validators/mongo` + `validators/postgres`).

### Foundation cross-cutting subsystems
- `incidents/` — captures and publishes throwables/incidents (authentication, registration, session, system) to external targets.
- `feigns/` — Feign clients for GitHub, OpenAI, Slack, Telegram, and Spring services; these back the notification/incident targets.
- `websockets/`, `filters/jwt/`, `tokens/`, `handshakes/` — the JWT + STOMP WebSocket security machinery configured via `jbst.security.websockets`.

### Running the servers locally
`run-mongodb.sh` / `run-postgres.sh` (IAM) and `run.sh` (hardware-monitoring) delegate to an external helper script `java-run-spring-boot-dev-profile-v4.sh` that must be on your `PATH`, and pass a Jasypt password (config values are Jasypt-encrypted, `PBEWithMD5AndDES`). Spin up the databases first via `docker/run-mongo.sh` or `docker/run-postgres.sh` (docker-compose). All apps use context path `/api`; Swagger UI lives at `{server}/api/swagger-ui/index.html` (dev IAM: http://localhost:3002/api/swagger-ui/index.html).

## CI and Release Engineering

- **CI** (`.github/workflows/main.yml`): on push to `main`, runs `mvn clean install` on Java 17 (Temurin). Maven `deploy` to GitHub Packages and the Docker image push are gated behind the `MAVEN_DEPLOYMENT_ENABLED` / `DOCKER_PUSH_ENABLED` env flags in that workflow (normally `false` on snapshots).
- **Version bumps are scripted** — do not hand-edit versions across POMs and the workflow. The repo follows a `SNAPSHOT:` → `RELEASE:` commit cadence:
  - `./next-release.sh` — strips `-SNAPSHOT` from all POMs and flips the workflow deploy/docker flags on.
  - `./next-snapshot.sh` — bumps to the next `-SNAPSHOT`, resets the workflow flags, resets `CHANGELOG.md`, and updates the `docker-compose.*.yml` image tags.
- `./gen-artifacts.sh` collects the foundation JAR + parent/foundation POMs into `artifacts/`.
- `./sonar-check.sh` requires a local SonarQube on `:9000`, then runs `mvn clean install` + `sonar-scanner` (config in `sonar-project.properties`).

## Conventions

- **Lombok** everywhere; `lombok.config` sets the logger field name to `LOGGER` (use `@Slf4j` then `LOGGER.info(...)`), adds `@lombok.Generated` (for coverage exclusion), and `@ConstructorProperties` on generated constructors.
- Server entrypoints are `Server.java` in each server module, extending `JbstStartupEventListener` and importing a `ConfigurationServer`.
