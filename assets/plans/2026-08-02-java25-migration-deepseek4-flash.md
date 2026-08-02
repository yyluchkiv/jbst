# Java 25 LTS Migration Plan — JBST

## Prerequisites & Scope

This codebase targets Java 21 via `<version.java>21</version.java>` (pom.xml:36) using
Maven's `<release>` flag (pom.xml:583), Lombok 1.18.46 (pom.xml:58), and Spring Boot
4.1.0 (pom.xml:61). No JPMS (`module-info.java` is absent). No preview features (String
Templates, ScopedValue, StructuredTaskScope, unnamed patterns) are in use — the
significant Java-21-specific patterns are virtual threads and `ReentrantLock`-based
carrier-thread pinning avoidance (JbstWorker.java:51, JbstLatencySynchronizedQueue.java:22),
both of which are final and stable in JDK 25.

---

## Required Mechanical Changes

These are pure configuration bumps — no source code changes are needed.

| Step | File | Change | Risk |
|------|------|--------|------|
| 1 | pom.xml:36 | `<version.java>21` → `<version.java>25` | None (single property propagates to compiler `<release>`, javadoc `<source>`, and all plugin references) |
| 2 | `.github/workflows/main.yml:37` | `java-version: '21'` → `java-version: '25'` | None (temurin 25 exists) |
| 3 | `.github/workflows/release.yml:27` | same | None |
| 4 | `jbst-server-iam/Dockerfile:1` | `FROM eclipse-temurin:21.0.11_10-jdk` → `FROM eclipse-temurin:25-jdk` | Low risk; verify image tag exists |
| 5 | `sonar-project.properties:7` | `sonar.java.source=21` → `sonar.java.source=25` | None |

---

## Items to Verify (Not Mechanical, Not Guesses)

### Build toolchain

- **maven-compiler-plugin 3.13.0** (pom.xml:69): Unverified with `--release 25`.
  The `forceLegacyJavacApi=true` flag (pom.xml:584) is required for Lombok delombok
  compatibility — verify it does not conflict with JDK 25's javac API changes.
- **lombok-maven-plugin 1.18.20.0** (pom.xml:77): The plugin is an older release; the
  bundled Lombok is overridden to 1.18.46 (pom.xml:554-560). Verify the plugin itself
  (not the Lombok library) runs correctly on JDK 25.
- **JaCoCo 0.8.12** (pom.xml:74): Unverified against JDK 25 bytecode.
- **japicmp 0.26.1** (jbst-foundation/pom.xml:210): Unverified reading JDK 25 classfiles.
  The baseline is a JDK-21-compiled jar; japicmp's bytecode-level comparison must
  handle the newer classfile format.

### Core framework

- **Spring Boot 4.1.0** (pom.xml:61): Unverified as a JDK 25 target. Boot 4.x targets
  JDK 21+; 4.1.0's specific JDK 25 compatibility is not confirmed here.
- **Testcontainers 2.0.5** (pom.xml:66): Unverified with JDK 25. The `--add-opens`
  flags to `java.time` and `java.math` (pom.xml:41, scripts) may behave differently
  or become unnecessary under JDK 25.
- **JUnit 6.0.3** (pom.xml:65): Unverified with JDK 25.

### Edge dependencies (low-activity upstreams)

- **browscap-java 1.4.4** (pom.xml:46): Last release 2022. Unverified with JDK 25.
- **JSch 0.2.23** (pom.xml:54): Low-activity. Unverified.
- **JColor 5.5.1** (pom.xml:55): Unverified.
- **jasypt-spring-boot 4.0.4** (pom.xml:53): Unverified.

These four are unlikely to break at the classfile level (they are pure-Java libraries
with no native code or bytecode generation), but integration testing is the only way
to be sure.

### JVM flags — review, not necessarily change

- `-XX:+ZGenerational` in `docker-compose.mongo.yml:15` and
  `docker-compose.postgres.yml:20`: This flag is the default in JDK 25 and may produce
  a JVM warning. Safe to keep (JDK accepts it silently in practice) or remove.
- `--add-opens java.base/java.time=ALL-UNNAMED` (pom.xml:41, run scripts, docker-compose
  files): Still needed if Testcontainers / the MongoDB driver reflectively access
  `java.time` internals on JDK 25. Verify; the flags are harmless if unnecessary.
- `--add-opens java.base/java.math=ALL-UNNAMED` (run-postgres.sh:18): Same logic.

---

## What Does NOT Change

| Feature | Status in codebase | Reason |
|---------|-------------------|--------|
| Records | Extensively used (20+ record classes) | Stable since JDK 16 |
| Pattern matching for `instanceof` | Used (JbstPropertyEdge.java:82,86) | Stable since JDK 16 |
| Arrow-case `switch` | Used (JbstNumbers.java, etc.) | Stable since JDK 14 |
| Virtual threads (`Thread.ofVirtual()`, `spring.threads.virtual.enabled`) | Heavily used (6 files, 2 server configs) | Stable since JDK 21; identical in JDK 25 |
| `ReentrantLock` to avoid carrier-thread pinning | JbstWorker.java:51, JbstLatencySynchronizedQueue.java:22 | Best practice, unchanged |
| No code changes to application logic | — | No source-level migration needed |

---

## Effort Estimate

| Activity | Effort |
|----------|--------|
| Apply 5 mechanical changes | <10 minutes |
| Verify build toolchain (compiler, Lombok, JaCoCo, japicmp) | 30–60 minutes |
| Verify Spring Boot + Testcontainers integration tests | 1–2 hours |
| Verify edge dependencies (Browscap, JSch, JColor, Jasypt) | 30 minutes (included in test suite run) |
| Review/clean up `-XX:+ZGenerational` and `--add-opens` flags | 15 minutes |
| **Total (assuming no blockers)** | **2–4 hours** |

If a dependency blocks (e.g., Browscap fails on JDK 25), the fallback is to bump it to
a newer release or exclude/replace it. If Spring Boot 4.1.0 does not support JDK 25,
the project is blocked until a Boot 4.x patch is released.

---

## Suggested Execution Order

1. Bump `<version.java>` and Sonar property, commit.
2. Bump CI workflow `java-version` and Docker base image, commit.
3. Run `./mvnw clean install` locally on JDK 25.
4. If compilation fails: diagnose plugin/Lombok issue; if tests fail: diagnose
   Testcontainers/dependency issue.
5. Run `./execute-unit-tests-only.sh` and `./execute-integrations-tests-only.sh`.
6. Clean up `-XX:+ZGenerational` / `--add-opens` flags if verified unnecessary.
7. Push and verify CI pass.