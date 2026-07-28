### Changelog [v1.73]
— docs: detailed migration plan to Spring Boot 4.1 (Framework 7, Jackson 3, JUnit 6, Testcontainers 2) — `assets/plans/2026-07-28-spring-boot-4-migration.md`
— refactor: JbstTelegram/JbstSlack send/edit workers migrated `new Thread` → `Thread.ofVirtual()` (daemon by definition)
— refactor: virtual-thread factories for scheduled executors (JbstWorker, JbstIncidentClientTypeTelegram); JbstSSH timeout connect on `newVirtualThreadPerTaskExecutor` with guaranteed shutdown (fixes executor leak on timeout/failure paths)
— refactor: `Collectors.toList()` → `Stream.toList()` (45 sites); note — returned lists are now unmodifiable; `JbstResponseUserSessionsTable.of` no longer sorts the passed list in place
— feat: Tomcat request handling on virtual threads via `spring.threads.virtual.enabled=true` (jbst-server-iam, jbst-server-hardware-monitoring)
— build: maven-compiler-plugin `<source>/<target>` → `<release>21</release>` (JDK API surface pinning)
— refactor: `String.format(...)` → `String.formatted(...)` (JbstEncryption, JbstHashing, JbstSSH)
