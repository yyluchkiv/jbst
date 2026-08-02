# Java 25 LTS Migration Plan

Scope: move JBST (multi-module Maven build, `jbst-foundation` library + `jbst-server-iam`
+ `jbst-server-hardware-monitoring` servers) from Java 21 to Java 25 LTS. Every claim
below is grounded in a repository file; anything I could not confirm from the repo is
marked **unverified**.

## Current state: where "21" actually lives

The Java version is already centralized, which makes this migration mostly mechanical:

- `pom.xml:36` — `<version.java>21</version.java>` is the single property. It feeds the
  compiler `<release>` (`pom.xml:583`) and the Javadoc `<source>` (`pom.xml:545`). One
  edit changes compilation for all three modules.
- `.github/workflows/main.yml:35-39` and `.github/workflows/release.yml:24-28` —
  `actions/setup-java@v5` with `java-version: '21'`, distribution `temurin`.
- `jbst-server-iam/Dockerfile:1` — base image `eclipse-temurin:21.0.11_10-jdk`
  (only the IAM server is containerized; `docker-build-locally.sh:23` builds this Dockerfile).
- `ship.sh:39-40` — local macOS convenience pin: `JAVA_HOME=$(/usr/libexec/java_home -v 21)`.
- `sonar-project.properties:7` — `sonar.java.source=21`.
- Documentation/comments only: `AGENTS.md:9`, `AGENTS.md:65`, `AGENTS.md:141`, and the
  "virtual threads (Java 21)" comments in `jbst-server-iam/src/main/resources/application.yml:3`
  and `jbst-server-hardware-monitoring/src/main/resources/application.yml:3`.

## Source-level compatibility: expected to be a non-event

I grepped the main sources for the APIs whose removal or hardening between 21 and 25
typically bites projects:

- No `SecurityManager` usage anywhere (JEP 486, disabled-for-good in JDK 24 — irrelevant here).
- No `sun.misc.Unsafe` usage (JEP 498 warnings in 25 — irrelevant for our own code).
- No custom `--add-exports`/agent/JNI in our own code; the only JVM flags are
  `--add-opens java.base/java.time=ALL-UNNAMED` (test `argLine` at `pom.xml:41`) and
  the same plus `java.math` in the dev run scripts (`jbst-server-iam/run-mongodb.sh:8`,
  `jbst-server-iam/run-postgres.sh:18`, `jbst-server-hardware-monitoring/run.sh:8`).
  These flags remain valid on 25 and should simply be kept.
- The codebase is already on Spring Boot 4.1.0 (`pom.xml:61`) and JUnit 6 / Testcontainers 2
  (`pom.xml:65-66`), i.e. a modern dependency baseline. The Spring Boot 4 / Spring
  Framework 7 line officially supports JDK 25; **unverified** for this exact patch
  version, but the line targets it.

Conclusion: no application-code changes are expected. The migration is a toolchain
migration, not a code migration.

## Toolchain and dependency risks (the real work)

Ordered by risk:

1. **Lombok / delombok — highest risk.** The build delomboks all sources before
   compilation (`pom.xml:550-576`, `lombok-maven-plugin` 1.18.20.0 pinned at
   `pom.xml:77`) and the POM already carries a workaround forcing project Lombok
   1.18.46 into the plugin because the bundled 1.18.20 had no JDK 21 support
   (`pom.xml:555-560`). Lombok hooks into javac internals, so every new JDK is a
   potential break. Whether Lombok 1.18.46 supports JDK 25 is **unverified** from
   this repo; whether a `lombok-maven-plugin` newer than 1.18.20.0 exists is
   **unverified**. Mitigation: bump `version.dependency.lombok` (`pom.xml:58`) to the
   newest release that advertises JDK 25 support (edge build if necessary) and keep
   the forced-dependency override; if the plugin itself breaks on javac 25, run
   delombok via the Lombok jar directly instead of the plugin.
2. **JaCoCo 0.8.12** (`pom.xml:74`) — JaCoCo must instrument class-file version 69
   (Java 25). 0.8.12 predates Java 25; the exact minimum version with 25 support is
   **unverified** (expected 0.8.13+). The `prepare-agent` execution (`pom.xml:620-636`)
   affects both Surefire and Failsafe runs, so a too-old JaCoCo fails the whole test
   phase, not just coverage reports.
3. **japicmp 0.26.1** (`pom.xml:75`, configured in `jbst-foundation/pom.xml:205-230`) —
   the `verify`-phase gate compares the fresh jar against the latest released
   jbst-foundation jar. After this migration the new jar is class-file 69 and the
   baseline is 65; japicmp must parse both. 0.26.1's support is **unverified**; bump
   if it errors. Note this comparison should not report a binary break from the class
   version alone — verify with a real `verify` run rather than assuming. The documented
   escape hatch `-Djapicmp.skip=true` exists but must not be used to silence a
   legitimate break (AGENTS.md rules).
4. **Maven Wrapper 3.9.9** (`.mvn/wrapper/maven-wrapper.properties:3`) — Maven itself
   will now run *on* JDK 25. 3.9.9 on JDK 25 is **unverified**; bumping the wrapper
   `distributionUrl` to the latest 3.9.x is cheap insurance.
5. **OSHI `oshi-core-java11` 6.6.6** (`pom.xml:59`, used in
   `jbst-foundation/src/main/java/jbst/foundation/domain/hardware/JbstHardware.java:8-29`) —
   JNA/native-based library. JDK 24+ emits restricted-native-access warnings for
   JNI without `--enable-native-access`; behavior on 25 is **unverified** but expected
   to be warnings, not failures. Watch the hardware-monitoring server logs after the
   upgrade.
6. **Remaining build plugins** — maven-compiler-plugin 3.13.0 (`pom.xml:69`) delegates
   to the JDK's javac, so `<release>25</release>` is low risk; surefire/failsafe 3.5.6,
   build-helper 3.6.0, git-commit-id 9.0.1, source/javadoc/deploy plugins — all
   expected fine on 25, **unverified**. Runtime libraries (jjwt 0.13.0, feign 13.13,
   liquibase 5.0.3, jasypt 4.0.4, guava, caffeine, springdoc 3.0.3) are plain bytecode
   and need nothing; **unverified** individually but very low risk.
7. **Downstream consumers of jbst-foundation.** The published jar becomes class-file
   69: anything consuming it must run on JDK 25+. This is a release-note item, and it
   interacts with the release cadence (`next-release.sh` / `release.yml`) — the first
   release after this migration should call out the runtime requirement.

## Ordered steps

1. **Pre-flight verification (no code changes).** Confirm from upstream release notes:
   newest Lombok with JDK 25 support; newest JaCoCo with class-file 69 support; japicmp
   handling of 69; latest Maven 3.9.x. Record chosen versions.
2. **Local environment.** Install Temurin JDK 25; confirm `/usr/libexec/java_home -v 25`
   resolves. Keep JDK 21 installed until the migration merges.
3. **Branch and minimal POM edits.** On an agent branch: set `<version.java>25</version.java>`
   (`pom.xml:36`), bump `version.dependency.lombok`, `version.plugin.jacoco`, and (if
   needed) `version.plugin.japicmp` in root `pom.xml` properties only (per AGENTS.md,
   all versions live there), and bump the wrapper `distributionUrl`.
4. **Compile.** `./compile-all.sh`. Expect any failure to come from delombok; fix per
   risk #1 before touching anything else. Per AGENTS.md, trust only the final
   `BUILD SUCCESS`, not delombok-phase noise — but a real delombok *failure* on javac 25
   is the one legitimate stop-the-line event here.
5. **Test.** `./execute-unit-tests-only.sh`, then `./execute-integrations-tests-only.sh`
   (Testcontainers Mongo + Postgres), then `./mvnw clean verify` so the japicmp gate
   runs. No test-skip flags; failures are reported, not worked around (AGENTS.md rules).
6. **CI.** Update `.github/workflows/main.yml:35-39` and
   `.github/workflows/release.yml:24-27` to `java-version: '25'` (and rename the
   `java21` step labels). Note: release scripts must never modify workflow files
   (`.github/deployment.env:1-5` explains the GITHUB_TOKEN restriction), but a normal
   PR editing workflows is fine.
7. **Docker.** Update `jbst-server-iam/Dockerfile:1` to the Temurin 25 JDK image, run
   `./docker-build-locally.sh`, and smoke-start the IAM server (mongo profile) plus the
   hardware-monitoring server; check startup logs for native-access (OSHI) and
   reflective-access warnings.
8. **Remaining pins and docs.** `ship.sh:40` → `-v 25`; `sonar-project.properties:7` →
   `sonar.java.source=25`; update `AGENTS.md:9`, `AGENTS.md:65`, `AGENTS.md:141` and the
   two `application.yml:3` comments.
9. **Changelog and ship.** Add `— build: migrate to Java 25 LTS` (with the two trailing
   spaces) under the current `### Changelog [v1.75]` heading in `CHANGELOG.md`, PR title
   `build: migrate to Java 25 LTS`, then ship via the normal ship workflow
   (`agents/workflows/ship.md`).
10. **Post-merge.** Confirm `main.yml` CI is green on 25. Before the next
    `release.yml` run, add the "requires JDK 25+ at runtime" note for consumers of
    jbst-foundation.

## Honest risks

- Delombok on javac 25 is a single point of failure with no workaround inside this
  repo; if neither a Lombok release nor edge build supports 25 at migration time, the
  migration blocks — there is no plan-B short of dropping delombok, which would be a
  build-architecture change, not part of this plan.
- If JaCoCo/japicmp 25-ready versions don't exist yet, `verify` cannot pass; better to
  wait than to disable the gates.
- Partial upgrades are the classic failure mode here: CI, Dockerfile, `ship.sh`, and
  Sonar pins must move together or the repo ends up compiling on 25 locally while CI
  still builds on 21 (or vice versa). Steps 6-8 are not optional polish.
- Runtime behavior changes in 25 (e.g. compact object headers, JEP 519) are upside,
  not risk, but heap/threading characteristics of the servers should be sanity-checked
  under the smoke run in step 7 rather than assumed identical.

## Effort estimate

1-2 working days total: roughly half a day of mechanical edits and verification
(steps 2-4, 6-8), half a day of full test + verify + Docker smoke runs, and up to a
day of buffer for Lombok/JaCoCo/japicmp version wrangling. If the pre-flight check in
step 1 shows a blocker (most likely Lombok), effort is zero until upstream catches up —
do not start the branch.
