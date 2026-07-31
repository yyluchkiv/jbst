# jbst

Instructions for AI coding agents working in this repository — the single source of
truth for any tool that reads agent instructions (Claude Code, Codex, Cursor, Gemini
CLI, etc.). `CLAUDE.md` is only a pointer to this file; edit this file, never the pointer.

## Project Overview

JBST is a Java 21 Spring Boot framework providing bootstrapping tools for enterprise applications. It is a multi-module Maven project:

- **jbst-foundation**: The shared library (JAR). Contains all domain objects, utilities, configurations, foundation services, the JWT-based IAM/security layer, the incidents subsystem, and Feign clients for external integrations. Both servers depend on it.
- **jbst-server-iam**: Identity & Access Management server. Runs against **either** MongoDB **or** PostgreSQL, selected by Spring profile. Port `3002`.
- **jbst-server-hardware-monitoring**: Hardware monitoring server. Port `6001`.

`jbst-foundation` is published/installed first; the servers consume it as a dependency. When iterating only on the library, `./build-fast.sh` installs just that module (`./mvnw -pl jbst-foundation -am clean install -DskipTests`).

## Rules

- **Never modify tests to make a build pass.** If tests fail, stop and report the
  failure. Maven test-skip flags (`-DskipTests`, `-Dmaven.test.skip`,
  `-Dsurefire.skip`, `-Dfailsafe.skip`, `-Dmaven.test.failure.ignore`, …) must not
  be used to get past failures — for Claude Code this is hook-enforced. The fast
  install scripts (`push-check.sh`, `build-fast.sh`) exist for local iteration
  speed; they are not a substitute for running the suite before shipping.
- **Never touch database migrations (Liquibase changelogs) on your own initiative**
  — `jbst-server-iam/src/main/resources/postgres/changelog.yml` + `changes/*.sql`,
  and `jbst-foundation/src/test/resources/db/changelog/**` (for Claude Code this is
  hook-enforced). If a task requires a schema change, stop and ask the user to make
  or explicitly authorize the changelog edit. The foundation test changelog is the
  live integration-test migration chain and serves as reference history; its init
  script stays baseline-only.
- Never change application logic when the task is a dependency upgrade.
- All dependency versions live in root `pom.xml` properties. Change them there only.
- Never edit public API signatures in jbst-foundation. This is enforced: japicmp fails
  `verify` on binary-incompatible changes vs. the latest release. Never add japicmp
  excludes or skip flags to get past it.
- If the build does not compile, stop and report the error. Do not work around it.

## Git

- Never push to `main` or `master`.
- Push only to your own agent branch namespace: `<agent>/*`
  (Claude Code → `claude/*`, Codex → `codex/*`).
- Never force-push.
- Do not add tool-attribution trailers or footers to commits or PRs
  (e.g. `Co-Authored-By: …`, `Generated with …`).

## Agent Workflows

Reusable, tool-neutral playbooks live in `agents/workflows/` — follow the matching
one when the user asks for that action, whatever agent tool you are running in:

- `plan-create.md` — write a plan file to `assets/plans/` for user review (no code).
- `plan-execute.md` — implement an approved, merged plan in its own PR.
- `ship.md` — ship the current branch as a self-merging squash PR (`./ship.sh`).
- `ship-fast.md` — same but skipping tests (`./ship.sh --fast`); only on explicit user request.
- `scrap.md` — abandon the current worktree (`./scrap.sh --yes`): delete the worktree, its branch, and any pushed remote branch without merging.

Keep workflow logic in these playbooks and the scripts they call. Tool-specific
commands (e.g. `.claude/skills/*`) are thin wrappers over them — when changing a
workflow, edit the playbook, not the wrappers.

## Build and Development Commands

All builds go through the **Maven Wrapper** (`./mvnw`, pinned to Maven 3.9.9 via `.mvn/wrapper/maven-wrapper.properties`) — no local Maven installation is required, only a JDK 21 (make sure `JAVA_HOME` points to one).

```bash
# Compile only (no tests)
./compile-all.sh                  # ./mvnw clean compile test-compile

# Unit tests only (Surefire)
./execute-unit-tests-only.sh      # ./mvnw clean test

# Integration tests only (Failsafe)
./execute-integrations-tests-only.sh   # ./mvnw failsafe:integration-test

# All tests (unit + integration)
./mvnw integration-test

# Full verification (what CI effectively runs)
./mvnw clean verify   # or `./mvnw clean install`

# Fast install, skip all tests
./push-check.sh                   # ./mvnw clean install -Dmaven.test.skip -DskipTests -T 4

# Library-only fast install
./build-fast.sh                   # ./mvnw -pl jbst-foundation -am clean install -DskipTests -T 4

# Local docker image for jbst-server-iam (fast install + docker build, tag = deployment.env DOCKER_VERSION)
./docker-build-locally.sh
```

### Running a single test
```bash
# Single unit test (class or method)
./mvnw test -Dtest=ClassName
./mvnw test -Dtest=ClassName#methodName

# Single integration test (Failsafe; *IT classes under src/test-integration/java)
./mvnw failsafe:integration-test -Dit.test=ClassNameIT#methodName
```

### Delombok build behavior (important)
The build runs the `lombok-maven-plugin` **delombok** goal before compilation, emitting expanded sources to `target/delombok` (used as the compile sourcepath, also feeding JaCoCo/Sonar). The delombok phase frequently prints alarming-looking errors/warnings even on a healthy build — **trust the final `BUILD SUCCESS`**, not delombok-phase noise.

### Binary-compatibility gate (japicmp)
`jbst-foundation` is a published library, so its `verify` phase runs the **japicmp-maven-plugin** (`jbst-foundation/pom.xml`, version property in the root POM): the freshly built jar is compared against the **latest released version**, auto-resolved from the `github-jbst` repository (GitHub Packages — needs `github-jbst` server credentials in `~/.m2/settings.xml`; CI gets them via `maven-settings-action`). Binary-incompatible public-API changes **fail the build**; source-incompatible ones are report-only; a missing/unresolvable baseline is skipped with a warning (`ignoreMissingOldVersion`), so builds without credentials still pass. Reports land in `jbst-foundation/target/japicmp/`; escape hatch: `-Djapicmp.skip=true`. Do not add japicmp excludes to silence a legitimate break — an intentional break is a `feat!:`/release decision.

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

PostgreSQL schema is managed by **Liquibase**: `src/main/resources/postgres/changelog.yml` `includeAll`s `postgres/changes/*.sql`. MongoDB has no migrations. When a data-model change is made (schema/changelog edits only on explicit user instruction — see Rules), update **both** the Mongo and Postgres paths — foundation mirrors them too (`repositories/mongo` + `repositories/postgres`, `services/mongo` + `services/postgres`, `validators/mongo` + `validators/postgres`).

### Foundation cross-cutting subsystems
- `incidents/` — captures and publishes throwables/incidents (authentication, registration, session, system) to external targets.
- `feigns/` — Feign clients for GitHub, OpenAI, Slack, Telegram, and Spring services; these back the notification/incident targets.
- `websockets/`, `filters/jwt/`, `tokens/`, `handshakes/` — the JWT + STOMP WebSocket security machinery configured via `jbst.security.websockets`.

### Running the servers locally
`run-mongodb.sh` / `run-postgres.sh` (IAM) and `run.sh` (hardware-monitoring) delegate to an external helper script `java-run-spring-boot-dev-profile-v4.sh` that must be on your `PATH`, and pass a Jasypt password (config values are Jasypt-encrypted, `PBEWithMD5AndDES`). Spin up the databases first via `docker/run-mongo.sh` or `docker/run-postgres.sh` (docker-compose). All apps use context path `/api`; Swagger UI lives at `{server}/api/swagger-ui/index.html` (dev IAM: http://localhost:3002/api/swagger-ui/index.html).

## CI and Release Engineering

- **CI** (`.github/workflows/main.yml`): on push to `main`, runs `./mvnw clean install` on Java 21 (Temurin). Maven `deploy` to GitHub Packages and the Docker image push are gated behind the `MAVEN_DEPLOYMENT_ENABLED` / `DOCKER_PUSH_ENABLED` flags in **`.github/deployment.env`** (loaded into `GITHUB_ENV` by the workflow; normally `false` on snapshots). The flags and `DOCKER_VERSION` live in that env file — not in `main.yml` — because the release workflow's `GITHUB_TOKEN` is not allowed to push commits that modify `.github/workflows/*`.
- **Version bumps are scripted** — do not hand-edit versions across POMs and `deployment.env`. The repo follows a `SNAPSHOT:` → `RELEASE:` commit cadence:
  - `./next-release.sh` — strips `-SNAPSHOT` from all POMs and the `docker-compose.*.yml` image tags, and flips the `deployment.env` deploy/docker flags on.
  - `./next-snapshot.sh` — bumps to the next `-SNAPSHOT`, resets the `deployment.env` flags, resets `CHANGELOG.md`, and updates the `docker-compose.*.yml` image tags.
  - Both scripts detect BSD vs GNU sed (`SED_INPLACE`) so they run on macOS and on Linux CI runners.
- **One-click release** (`.github/workflows/release.yml`, Actions → `release` → *Run workflow*): automates the whole cadence on a runner — guards (version must be `-SNAPSHOT`, tag must not exist, `CHANGELOG.md` must not contain `— TBD`), extracts release notes from `CHANGELOG.md`, runs `next-release.sh`, deploys with `./mvnw clean -DskipTests -Dmaven.test.skip deploy -Pgithub` (no tests, same as the `main.yml` deploy step — `main` is assumed green from regular CI; a failed build still aborts before anything is pushed), commits/pushes `RELEASE: vX.Y`, creates GitHub release + tag `vX.Y` with the notes, then runs `next-snapshot.sh` and commits/pushes `SNAPSHOT: vX.(Y+1)`. The two bot commits do **not** trigger `main.yml` (GITHUB_TOKEN pushes don't start workflows) — deployment already happened inside the release run.
- `./gen-artifacts.sh` collects the foundation JAR + parent/foundation POMs into `artifacts/`.
- `./sonar-check.sh` requires a local SonarQube on `:9000`, then runs `./mvnw clean install` + `sonar-scanner` (config in `sonar-project.properties`).

## Conventions

### Changelog and commit messages — Conventional Commits
Both `CHANGELOG.md` lines and commit subjects use **Conventional Commits** types:

| Type | Use for |
|---|---|
| `feat` | New functionality (formerly `addition:`) |
| `fix` | Bug fixes |
| `refactor` | Code restructuring without behavior change (formerly `modification:`, `deletion:` of internals) |
| `perf` | Performance improvements |
| `docs` | Documentation only (README, AGENTS.md, Javadoc) |
| `test` | Adding or fixing tests |
| `build` | Build system / dependencies (Maven, wrapper, scripts) |
| `ci` | GitHub Actions workflow changes |
| `chore` | Maintenance that fits nothing above |

- Changelog line format: `— type: short description` (one line per change, appended under the current `### Changelog [vX.Y]` heading; replace the initial `— TBD` seeded by `next-snapshot.sh`). **End every changelog line with two trailing spaces** — the release workflow copies the body into the GitHub release notes, and without the markdown hard break all changes collapse into one paragraph. Verify with `grep -c '  $' CHANGELOG.md`.
- Commit subject format: `type: short description` (scope optional: `feat(iam): ...`); append `!` for breaking changes (`feat!: ...`) and mention the break in the changelog line.
- `SNAPSHOT: vX.Y` / `RELEASE: vX.Y` commits are produced by the release scripts and are exempt.
- When a change lands via PR, use the same `type:` prefix in the PR title.

- **`fixed()` factory methods**: domain objects, DTOs, and properties classes should expose a static `fixed()` factory (plus variants like `fixed(...)` overloads or `fixedMasked()`) returning a deterministic, fully-populated instance — used to simplify building and testing (test fixtures, `TestJbstConfigurationPropertiesFixed`, dev-time endpoints). When adding such a class, add a `fixed()` method alongside any `random()` factory. Formerly named `hardcoded()` — use `fixed()` for all new code.
- **Lombok** everywhere; `lombok.config` sets the logger field name to `LOGGER` (use `@Slf4j` then `LOGGER.info(...)`), adds `@lombok.Generated` (for coverage exclusion), and `@ConstructorProperties` on generated constructors.
- Server entrypoints are `Server.java` in each server module, extending `JbstStartupEventListener` and importing a `ConfigurationServer`.
