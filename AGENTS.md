# jbst

Multi-module Maven project. Java 21.

## Build
./mvnw -B -q clean verify

Single module:
./mvnw -B -q -pl jbst-foundation -am verify

## Modules
- jbst-foundation — core library
- jbst-server-iam — IAM server
- jbst-server-hardware-monitoring — monitoring server

## Rules
- Never modify tests to make a build pass. If tests fail, stop and report.
- Never change application logic when the task is a dependency upgrade.
- All dependency versions live in root pom.xml properties. Change them there only.
- Never touch database migrations (Liquibase changelogs).
- Never edit public API signatures in jbst-foundation.
- If the build does not compile, stop and report the error. Do not work around it.
