# Plan: migrate JBST from Java 17 to Java 21

## Goal and boundary

Move the entire Maven reactor—`jbst-foundation`, `jbst-server-iam`, and
`jbst-server-hardware-monitoring`—to **Java 21 LTS** for compilation, tests,
CI, container runtime, and the installed hardware-monitoring service.

This is deliberately a platform migration, not a framework or feature rewrite:

- Compile with Java 21 bytecode and run all deployable artifacts on Java 21.
- Keep Spring Boot at the repository's current `3.4.2` initially. Do not bundle a
  Spring Boot, database, API, or virtual-thread migration into this change.
- Preserve the current public APIs, YAML binding, persistence schemas, and
  MongoDB/PostgreSQL dual-backend behaviour.
- Remove or repair only Java-21 incompatibilities and behaviour changes found
  by the defined verification gates.
- Consider adopting Java 21 features, especially virtual threads, only in a
  separate, measured follow-up after this migration is released.

The desired end state is a clean `mvn clean verify` (or its checked-in wrapper
equivalent) under JDK 21, a Java-21 CI build, Java-21 IAM container, and a
Java-21 systemd installation. A Java-17 runtime is **not** supported after the
compiler release is raised to 21.

## Repository-specific baseline

The following is the migration inventory as inspected on 2026-07-28. It is the
source of truth for the implementation work; re-run the searches in Step 1 if
the branch has moved materially before implementation begins.

| Area | Current state | Consequence |
| --- | --- | --- |
| Reactor | Parent `pom.xml` plus `jbst-foundation`, `jbst-server-iam`, and `jbst-server-hardware-monitoring` | One atomic migration; the servers consume the foundation JAR at the same project version. |
| Java build target | `<version.java>17</version.java>`; compiler uses `<source>` and `<target>`; Javadoc uses `<source>` | Change the target to 21 and use the compiler plugin's `release` setting. |
| Compiler API | `maven-compiler-plugin` 3.13.0 has `<forceLegacyJavacApi>true</forceLegacyJavacApi>` | Remove the forced legacy `com.sun.tools.javac` path and use the default `javax.tools` API. |
| Annotation processing | Compile dependency is Lombok `1.18.36`, but `lombok-maven-plugin` is `1.18.20.0` and runs Delombok before compile/Javadoc/Sonar | This is a release blocker: Lombok added initial JDK 21 support in 1.18.30; the old Delombok plugin bundles Lombok 1.18.20. |
| Quality gate | JaCoCo `0.8.12`, Surefire/Failsafe `3.5.2`, Testcontainers `1.20.4`, Sonar `sonar.java.source=17` | Verify all tools under JDK 21; update Sonar's source level to 21. JaCoCo 0.8.12 already advertises support through Java 23 class files. |
| CI | `.github/workflows/main.yml` installs Temurin 17 in a step named `java17` | Change both the step label and the installed Java version. |
| IAM image | `jbst-server-iam/Dockerfile` starts from `eclipse-temurin:17.0.7_7-jdk-alpine` | Rebuild and smoke-test a maintained Temurin 21 Alpine image. |
| Installed HM service | `files/installations/jbst-server-hardware-monitoring/hms.service` sets `JAVA_HOME=/opt/jdk-17.0.7` | Change the unit and validate the deployed JDK 21 path on the target host. |
| Test layout | 215 unit and 14 Testcontainers integration Java tests in foundation; 23 IAM and 6 hardware-monitoring Java sources | Validate the full shared library first, then both server packages and both data stores. |
| Runtime module access | Parent test `argLine` opens `java.time`; run scripts and Docker also open `java.time`; IAM PostgreSQL script additionally opens `java.math` | Treat each opening as a compatibility hypothesis. Do not remove an opening until the scoped test/runtime scenario passes without it. |
| Local tooling | JDK `21.0.10` and `javac` are available in the inspected environment; Maven is not installed and there is no Maven Wrapper | A local baseline could not be run here. Establish a reproducible Maven command before changing the project. |

### Architecture and test impact

`jbst-foundation` is the primary risk surface: 433 production Java sources
provide configuration, Spring Security/JWT, REST/WebSocket, incidents,
Feign clients, MongoDB/JPA repositories, and shared domain types to both
servers. A structural map found the most connected abstractions to be
`Username`, `JbstRandom`, `Email`, `JbstProperties`, `JbstJwtUser`, and
`JbstConstants`; regressions involving these types must be exercised through
both server modules, not only compiled.

The relevant executable paths are:

```text
jbst-foundation JAR
  ├── jbst-server-iam (Spring profiles: mongodb and postgres; port 3002)
  └── jbst-server-hardware-monitoring (port 6001)

GitHub Actions → Maven install/deploy → IAM Docker image
                                      → foundation package artifact
systemd hms.service → Java runtime → hardware-monitoring JAR
```

## Known risks and required remediations

### P0 — make Delombok Java-21 capable before compiling

`pom.xml` uses `org.projectlombok:lombok-maven-plugin:1.18.20.0`. That plugin
brings Lombok 1.18.20, while the application dependency is already 1.18.36.
The plugin runs in `generate-sources` and its output is consumed by Javadoc,
JaCoCo/Sonar conventions, and compilation source paths. It cannot be treated
as an incidental optional tool.

Implement the following proof of compatibility before changing the Java target:

1. Run the current Delombok goal on JDK 21 with Maven debug output, recording
   the resolved plugin dependency tree.
2. Prefer a current, supported Delombok invocation that resolves Lombok
   `>= 1.18.30` (the project already declares 1.18.36). Do **not** merely
   change a version number unless `help:effective-pom` proves that the plugin
   actually uses that Lombok JAR.
3. If a plugin-dependency override is stable, declare it explicitly and verify
   that `target/delombok` is generated from Lombok 1.18.36. If it is not
   stable, replace the stale plugin with a supported build step that invokes the
   same versioned Lombok JAR's `delombok` command.
4. Keep the generated directory out of source control, preserve
   `addOutputDirectory=false`, and ensure generated sources are cleaned by
   `mvn clean`.
5. Run compile, unit tests, Javadoc attachment, JaCoCo report generation, and
   Sonar analysis after the replacement. A compile-only success is insufficient.

**Exit criterion:** the Maven log identifies Lombok 1.18.30 or newer for both
annotation processing and Delombok, and all generated-source consumers pass
on JDK 21.

### P0 — remove the broken final-field reflection hook or make callers explicit

`jbst-foundation/src/main/java/jbst/foundation/domain/reflection/JbstReflections.java`
has `objectFieldHook(...)`, which calls
`Field.class.getDeclaredField("modifiers")` before clearing `final`. On the
installed JDK 21, `Field.class.getDeclaredFields()` is empty and that exact
lookup throws `NoSuchFieldException`; this was reproduced independently with
JShell. `--add-opens` will not restore a field that no longer exists.

1. Search all production and test callers before editing. The initial scan
   found no caller of `objectFieldHook`; do not assume that remains true.
2. If still unused, delete the method and its now-unused `Modifier` import, then
   add a focused regression test asserting the supported reflection helpers
   still work for application-owned fields.
3. If a caller exists, redesign its seam: inject the dependency, use a package-
   visible test seam, or replace an immutable configuration object rather than
   mutating a `static final` field. Do not introduce `Unsafe`, deep JDK
   reflection, or further `--add-opens` flags to preserve it.
4. Keep the ordinary helpers (`setPrivateField`, superclass access, getter
   discovery) only for application classes. Replace `setAccessible(true)` with
   `trySetAccessible()` where failure needs a controlled diagnostic, and test
   it against the actual target type.

**Exit criterion:** no source references `Field.modifiers`; every reflection
helper used by tests works on JDK 21 without access to JDK internals.

### P1 — make encoding contracts explicit

JDK 18 made UTF-8 the default charset. This repo has several places that rely
on the platform default; Windows or non-UTF-8 Linux deployments can therefore
change behaviour relative to Java 17.

| File | Current implicit conversion | Required Java-21 action |
| --- | --- | --- |
| `domain/cryptography/JbstEncryption.java` | `cipher.doFinal(value.getBytes())` and `new String(original)` | Use `UTF_8` for both calls. Add encrypt/decrypt tests containing non-ASCII text and assert Java-17-compatible ciphertext only if that compatibility is a documented requirement. |
| `assistants/utils/JbstSecurityUtils.java` | JWT secret `getBytes()` | Use an explicit documented charset, normally `UTF_8`; add a test covering the configured-secret bytes. |
| `domain/cryptography/JbstEncoding.java` | Basic-auth string `getBytes()` | Use the chosen protocol charset explicitly; verify the remote-service contract before selecting UTF-8. |
| `domain/http/cache/JbstCachedBodyHttpServletRequest.java` | `InputStreamReader(...)` and cached-payload conversion use defaults | Use the request character encoding when present, with a documented fallback; test UTF-8 and a non-UTF-8 declared request body. |
| `domain/ssh/JbstSSH.java` | `InputStreamReader(in)` uses host default | Define the remote-command output charset in configuration or use explicit UTF-8 only after validating all managed hosts. |
| `domain/random/JbstRandom.java` | Test Feign request uses `Charset.defaultCharset()` | Use `UTF_8` so test data is deterministic across CI agents. |

Do not add a global `-Dfile.encoding=COMPAT` workaround: it hides an
environment-dependent application contract instead of fixing it. Preserve the
existing explicit UTF-8 usages in email and hashing code.

### P1 — scope, then reduce, module-opening flags

The existing flags are intentional historical compatibility workarounds, not
automatically obsolete:

- Parent Surefire/Failsafe `argLine`: `--add-opens java.base/java.time=ALL-UNNAMED`.
- IAM MongoDB development runner, HM runner, and Docker Compose files: the
  corresponding `java.time` opening.
- IAM PostgreSQL development runner: `java.time` plus `java.math`.

Keep the flags for the first Java-21 build. Then execute the test matrix while
removing exactly one opening at a time, capture the full exception and
dependency causing it, and either upgrade/replace that dependency or retain a
single narrowly scoped documented flag. Never copy a test-only opening to a
production image by default. The final plan must distinguish:

1. Maven test JVM flags;
2. developer-runner flags;
3. IAM container flags; and
4. the HM systemd runtime flags.

### P1 — verify the full dependency and plugin graph

Do not indiscriminately upgrade the dependency management block while changing
the JDK. Generate and retain these reports from a clean JDK-21 build:

```bash
./mvnw -B -ntp help:effective-pom -Doutput=target/effective-pom.xml
./mvnw -B -ntp dependency:tree -Dverbose -DoutputFile=target/dependency-tree.txt
./mvnw -B -ntp help:effective-settings -Doutput=target/effective-settings.xml
```

Review, in priority order:

- Lombok runtime/annotation-processor/Delombok resolution (mandatory).
- Maven Compiler 3.13.0 after removal of `forceLegacyJavacApi`; Java 21 has a
  supported `javax.tools` compiler path.
- Spring Boot 3.4.2 and its managed Spring Data/JPA/MongoDB/Tomcat stack.
- Hibernate metamodel generator 6.6.5.Final, Hypersistence
  `hypersistence-utils-hibernate-63`, PostgreSQL, Liquibase, and MongoDB
  driver through the PostgreSQL and Mongo integration suites.
- Testcontainers 1.20.4, Surefire/Failsafe 3.5.2, and JaCoCo 0.8.12.
- Jasypt, JJWT, Feign/OkHttp, JSch, OSHI, GeoIP, and Springdoc through each
  service smoke test.

Upgrade an individual component only when it blocks Java 21 or has a verified
Java-21 defect. Record the reason, before/after resolved version, and affected
test in the migration PR.

## Execution plan

### Step 0 — create a safe migration branch and decision record

1. Create a branch such as `codex/java-21-migration` from a clean, current
   `main`; do not combine unrelated feature work.
2. Copy this plan into the pull request description and create a check list for
   every acceptance criterion below.
3. Choose the supported distribution once for all environments: Temurin 21
   LTS is the natural match because CI already uses Temurin. Pin to a current
   vendor CPU patch/digest at implementation time; do not retain the
   EOL `17.0.7_7` image tag.
4. Define the support matrix: developer macOS/Linux, GitHub `ubuntu-latest`,
   IAM Docker Alpine/Linux, and the hardware-monitoring Linux host. Confirm
   that each has an available JDK 21 build for its architecture.
5. State the compatibility contract in the PR: foundation artifacts compiled
   for release 21 require Java 21+ consumers. If external consumers still need
   Java 17, split that compatibility decision into a separately versioned
   release; do not claim one JAR supports both release levels.

### Step 1 — establish reproducible Java-17 and Java-21 baselines

1. Add a Maven Wrapper pinned to an approved Maven 3.9.x release, then change
   repository shell scripts and CI commands to use `./mvnw`. This is recommended
   because the inspected environment has JDK 21 but no `mvn`, and the current
   repository otherwise does not pin a Maven version.
2. Before changing `pom.xml`, run the current branch with a JDK 17 toolchain in
   a controlled environment. Archive command output, test totals, failed/skipped
   tests, packaged artifact names, `java -version`, `javac -version`, and
   `./mvnw -version`.
3. Run the exact existing gates:

   ```bash
   ./mvnw -B -ntp clean compile test-compile
   ./mvnw -B -ntp clean test
   ./mvnw -B -ntp clean verify
   ```

   The final command is the baseline for the full reactor. It runs foundation's
   Failsafe configuration and the MongoDB/PostgreSQL Testcontainers suites.
4. Run the IAM server under `mongodb` and `postgres` profiles and the HM server
   once on Java 17 if an environment is available. Record health, actuator
   info, Swagger availability, authentication/JWT flow, a WebSocket handshake,
   and database read/write behaviour.
5. Capture the Java-17 native encoding on every non-UTF-8 candidate host with:

   ```bash
   java -XshowSettings:properties -version 2>&1 | grep native.encoding
   ```

6. Repeat the three Maven commands with JDK 21 **before** changing source or
   configuration. Classify every failure as Delombok, compiler, test framework,
   library/runtime, or application behaviour. This separates JDK incompatibility
   from changes introduced by the migration itself.

If JDK 17 is unavailable locally, execute this step in a temporary CI job or
container; do not fabricate a passing baseline. The current workspace could
not perform it because Maven is not installed.

### Step 2 — make the parent build target Java 21

Edit the parent [`pom.xml`](../../pom.xml) only after the baseline is archived:

1. Change `<version.java>` from `17` to `21`.
2. Add the standard compiler property
   `<maven.compiler.release>${version.java}</maven.compiler.release>` and
   configure the compiler plugin with `<release>${version.java}</release>`.
   Remove the separate `<source>` and `<target>` configuration so the compiler
   checks both language level and Java 21 platform APIs. This avoids a class
   accidentally using a newer API while targeting a mismatched bytecode level.
3. Delete `<forceLegacyJavacApi>true</forceLegacyJavacApi>`. Compiler plugin
   3.13.0 supports the standard compiler API; retaining the forced legacy path
   makes the build depend on internal compiler implementation details.
4. Retain `<parameters>true>`, warning/deprecation visibility, source encoding,
   the custom delombok output directory, source attachment, and current test
   source registration. They are unrelated to the Java level.
5. Change the Javadoc source/release setting to 21 and run the attached
   Javadoc goal. If it cannot consume the Delombok output, fix the Delombok
   strategy in the P0 item rather than disabling Javadocs or setting
   `doclint=none` more broadly.
6. Do not change module POM coordinates or their foundation dependency range.
   All three modules inherit the parent target and should remain version-aligned.
7. Confirm the resulting classes with:

   ```bash
   javap -verbose jbst-foundation/target/classes/jbst/foundation/domain/base/Username.class \
     | grep 'major version'
   ```

   Expected Java 21 class-file major version: `65`.

### Step 3 — repair build-time annotation processing and generated sources

Implement the P0 Delombok proof of compatibility before treating any compile
failure as an application-source problem.

1. Inspect `./mvnw -X generate-sources` and prove the exact Lombok version
   doing Delombok.
2. Upgrade or replace only the stale Delombok mechanism, keeping the project
   Lombok dependency and annotation processor at the same Java-21-capable
   version.
3. Delete and regenerate `target/delombok`; compare a small representative
   sample of generated classes using `@Data`, `@Builder`, `@Slf4j`, records, and
   `@UtilityClass` against the current output. Pay particular attention to the
   configured logger field name `LOGGER` and `@lombok.Generated` coverage
   suppression in `lombok.config`.
4. Run `clean test-compile`, `javadoc:jar`, `jacoco:report`, and the Sonar
   preparation path. Ensure Sonar still receives `target/classes`, copied
   Lombok, test reports, and `target/site/jacoco/jacoco.xml`.

### Step 4 — fix source-level Java-21 behaviours and reflection

Apply the P0 reflection remediation and P1 charset repairs above in isolated,
reviewable commits.

1. Add direct regression tests for every changed conversion/reflective helper;
   test values should include non-ASCII characters and an explicit non-UTF-8
   input when an inbound protocol allows it.
2. Search before and after each commit for JDK internals and risky reflection:

   ```bash
   rg -n --glob '*.java' '(sun\\.|com\\.sun\\.|jdk\\.internal\\.|Unsafe\\b|Field\\.modifiers|setAccessible\\()' \
     jbst-foundation/src jbst-server-iam/src jbst-server-hardware-monitoring/src
   ```

3. After packaging, run static JDK-internal and removal scans:

   ```bash
   jdeps --multi-release 21 --ignore-missing-deps --jdk-internals \
     jbst-foundation/target/jbst-foundation-*.jar
   jdeprscan --release 21 --for-removal \
     jbst-foundation/target/jbst-foundation-*.jar
   ```

4. Investigate every finding. A `jdeps`-clean report is necessary but not
   sufficient: reflection (for example the existing classpath scan for
   `AbstractAuthority`) must still be exercised in integration tests.
5. Do not rewrite records, `var`, Jakarta imports, or Spring configuration just
   to use new Java syntax; the project already uses Java-17-era records and
   Jakarta APIs successfully.

### Step 5 — update delivery, quality, and developer environments

Make the following explicit file edits in one delivery-focused commit after the
JDK-21 build passes locally:

| File | Change |
| --- | --- |
| `.github/workflows/main.yml` | Rename step `java17` to `java21`; set `actions/setup-java` `java-version: '21'`; retain Temurin and Maven cache. Switch commands to `./mvnw` if Step 1 adds the wrapper. |
| `jbst-server-iam/Dockerfile` | Replace the Java 17 base with a maintained Temurin 21 JDK Alpine image, ideally digest-pinned. Preserve the current entrypoint arguments initially; rebuild the fat JAR with the Java-21 reactor. |
| `files/installations/jbst-server-hardware-monitoring/hms.service` | Point `JAVA_HOME` to the deployed JDK 21 directory, keeping `ExecStart` bound to `$JAVA_HOME/bin/java`. Confirm ownership, executable paths, and restart policy on the target host. |
| `sonar-project.properties` | Change `sonar.java.source=17` to `21`; validate the server/scanner supports Java-21 analysis. |
| `README.md` | Change the project description from `java17` to `java21`; document the required Java 21 and Maven Wrapper commands. |
| `CLAUDE.md` | Update the Java 17 overview and CI statements, build prerequisites, and any Java-version-specific operational guidance. |
| `BACKLOG.md` | Replace the pending `Migration @ Java21` entry only after the release is complete; do not mark it complete when the branch merely compiles. |

The following files need review but not an automatic version substitution:

- `jbst-server-iam/run-mongodb.sh`, `jbst-server-iam/run-postgres.sh`, and
  `jbst-server-hardware-monitoring/run.sh`: validate their external
  `java-run-spring-boot-dev-profile-v4.sh` helper chooses JDK 21, then update
  documentation or helper configuration outside this repository as required.
- `files/docker/docker-compose.mongo.yml` and
  `files/docker/docker-compose.postgres.yml`: no JDK image is named directly,
  but both pass `JVM_ARGUMENTS`. Test those flags against the new IAM image and
  remove only proven-unnecessary openings.
- Database Docker image versions, Liquibase SQL, and YAML properties: no change
  is expected solely because of Java 21. Their regression tests are mandatory.

### Step 6 — execute the compatibility and regression matrix

Run every command with JDK 21 and archive results in the PR/CI artifacts. Use
`-B -ntp` in CI for deterministic non-interactive logs.

| Gate | Command / scenario | Required evidence |
| --- | --- | --- |
| Toolchain | `./mvnw -version`, `java -version`, `javac -version` | Maven executes with a JDK 21 compiler, not a JRE or a JDK 17 toolchain. |
| Generated sources | `./mvnw -B -ntp clean generate-sources` | Java-21-capable Delombok output; no generated sources committed. |
| Compile | `./mvnw -B -ntp clean compile test-compile` | All parent/module production and test sources compile. |
| Unit tests | `./mvnw -B -ntp clean test` | Existing pass/fail/skip counts match the Java-17 baseline except for intentional newly enabled regression tests. |
| Integration tests | `./mvnw -B -ntp -pl jbst-foundation -am clean verify` | Testcontainers MongoDB and PostgreSQL repository suites pass. Docker is available and reusable-container behaviour is documented. |
| Full reactor | `./mvnw -B -ntp clean verify` | Repackage, Failsafe verify, sources, Javadocs, and JaCoCo report all succeed across the three modules. |
| Dependency health | `dependency:tree`, `jdeps`, `jdeprscan` from Steps 1 and 4 | No unapproved JDK internal API or removal finding. |
| Sonar | `./sonar-check.sh` or its wrapper-based equivalent against the supported server | Java source level 21, test reports, binaries, Lombok library, and JaCoCo XML are accepted. |
| IAM Mongo smoke | Start Mongo Compose and IAM with `mongodb` profile | `/api/actuator/health`, Swagger, login/JWT/cookie flow, protected endpoint, and graceful shutdown work. |
| IAM PostgreSQL smoke | Start PostgreSQL Compose and IAM with `postgres` profile | Health/API flows plus Liquibase schema startup and representative database operation work. |
| HM smoke | Run the JDK-21 jar and then the installed systemd unit | Health/API/hardware endpoint works; `systemctl status` reports the expected JDK 21 process. |
| WebSocket/security | Connect through the configured STOMP endpoint using JWT/CSRF requirements | Authentication and handshake behaviours match baseline. |
| Encoding matrix | Execute the new charset tests and at least one run with `-Dfile.encoding=COMPAT` during diagnosis | Behaviour is intentional and identical where compatibility is required; no default-charset dependency remains. |
| Module flags | Test Maven, each runner script, Compose, and systemd with opening flags scoped one by one | Retained flags have an owning scenario and documented reason; no unnecessary production opening remains. |

For service smoke tests, capture startup logs, effective `java -version` from the
container/host, HTTP status and JSON response excerpts with secrets redacted,
and the exact image digest or JDK package version. Do not log the Jasypt
passwords present in the development scripts.

### Step 7 — validate CI, packaging, and release mechanics

1. Open a PR and require the updated GitHub Actions workflow to run the full
   Java-21 `clean install` path. Confirm Maven cache restoration does not reuse
   Java-17 compiled output (Maven targets should be cleaned).
2. Exercise the `MAVEN_DEPLOYMENT_ENABLED=true` deployment path in an approved
   non-production context. Verify the published foundation POM/JAR declares
   Java 21 bytecode and both server modules resolve the matching foundation
   version.
3. Build the IAM Docker image from `jbst-server-iam/`, run it using both Compose
   configurations, and inspect `java -version` inside the final container.
4. Run `./gen-artifacts.sh` (updated to wrapper use if applicable) and inspect
   the copied foundation JAR, parent POM, and foundation POM. Ensure no
   `target/`/Delombok outputs leak into `artifacts/`.
5. Exercise `./next-release.sh` and `./next-snapshot.sh` only in a disposable
   branch or dry-run equivalent. Their version and workflow-flag substitutions
   must leave the Java-21 workflow configuration intact.
6. Re-run the full check from a fresh clone or clean CI worker to prove it does
   not rely on a developer's JDK 17 installation, local Maven state, or local
   Testcontainers cache.

### Step 8 — release, observe, and retain a rollback path

1. Release foundation and both server artifacts together; their same-version
   coupling means a piecemeal production rollout creates avoidable classpath
   risk.
2. Before deployment, retain the last successful Java-17 image/JAR, its exact
   configuration, and a tested deployment procedure. Because the new artifacts
   are release-21 bytecode, rollback means redeploying that complete Java-17
   release, not attempting to start a Java-21 artifact on Java 17.
3. Deploy one IAM instance/canary first for each active database profile. Watch
   startup, GC/heap, HTTP error rate, authentication failures, WebSocket errors,
   Liquibase state, and incident output for a defined observation window.
4. Deploy hardware monitoring after IAM is stable; verify the systemd service
   uses the desired `JAVA_HOME` and restarts cleanly.
5. Promote only after all functional, operational, and encoding acceptance
   criteria pass. Close the backlog item and record the final runtime image/JDK
   version, retained module-opening flags, and any dependency upgrades.

## Exact implementation order and commit boundaries

Keeping commits narrow makes failures bisectable and lets reviewers distinguish
platform changes from behavioural changes:

1. `build: add reproducible Maven/JDK-21 toolchain` — Maven Wrapper (if
   accepted), baseline documentation, CI toolchain only; no target change.
2. `build: make delombok compatible with Java 21` — complete the P0 Delombok
   proof and generated-source tests while source target is still 17 if possible.
3. `build: target Java 21` — parent POM `release`, Javadoc level, compiler API,
   Sonar level, and CI Java 21.
4. `fix: remove unsupported final-field reflection` — isolated code/test change.
5. `fix: make character encodings explicit` — isolated code/test changes.
6. `build: run JDK-21 delivery environments` — Dockerfile, systemd unit,
   runners/Compose decisions, README/CLAUDE, and release scripts.
7. `test: record Java-21 verification evidence` — no production change unless
   a test reveals a scoped compatibility fix.

Do not merge a commit that weakens or skips Javadocs, Sonar, integration tests,
or module-access checks merely to make the first Java-21 compile pass.

## Acceptance criteria

- [ ] Parent and all module artifacts compile with `--release 21` (class-file
      major version 65).
- [ ] A JDK 21 distribution and Maven version are reproducible in local and CI
      builds; no build silently uses JDK 17.
- [ ] Delombok/annotation processing uses Lombok 1.18.30 or newer and all its
      generated-source consumers pass.
- [ ] `JbstReflections.objectFieldHook` no longer depends on `Field.modifiers`.
- [ ] All default-charset conversion sites have a documented explicit contract
      and regression coverage where behaviour was changed.
- [ ] The full unit, Failsafe MongoDB, Failsafe PostgreSQL, Javadoc, JaCoCo,
      package, Sonar, and static JDK API scans pass on JDK 21.
- [ ] Java 21 is configured in GitHub Actions, the IAM image, and the HM systemd
      installation; runtime smoke tests prove the new JDK is actually used.
- [ ] Every retained `--add-opens` flag has a test/runtime owner and a written
      reason; flags not needed under Java 21 are removed from their specific
      scope.
- [ ] IAM works with both `mongodb` and `postgres` profiles, and HM works as a
      standalone server, with security/WebSocket flows matching the baseline.
- [ ] Java-17 artifact/image rollback is documented and has been rehearsed.

## References

- [Oracle JDK 21 migration preparation guide](https://docs.oracle.com/en/java/javase/21/migrate/preparing-migration.html) — recommends running on the target JDK, updating third-party tools, using `jdeps`, and explicitly calls out the JDK 18 UTF-8 default-charset change.
- [Apache Maven Compiler Plugin: source/target guidance](https://maven.apache.org/components/plugins/maven-compiler-plugin/examples/set-compiler-source-and-target.html) — recommends the `maven.compiler.release` property for Compiler Plugin 3.13.0+.
- [Apache Maven Compiler Plugin 3.13.0 `forceLegacyJavacApi` documentation](https://maven.apache.org/plugins-archives/maven-compiler-plugin-3.13.0/compile-mojo.html) — documents the standard compiler API and the legacy override currently forced by this repository.
- [Project Lombok changelog](https://projectlombok.org/changelog) — records initial JDK 21 support in Lombok 1.18.30.
- [JaCoCo change history](https://www.jacoco.org/jacoco/trunk/doc/changes.html) — records Java 23 class-file support in the repository's JaCoCo 0.8.12 release.
