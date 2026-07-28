### Changelog [v1.70]
— fix: dev/docker configs — remove stale `SERVER` incidents-manager type (enum only has `TELEGRAM` now); incidents-manager disabled locally, fixes `run-postgres.sh`/`run-mongo.sh` docker startup
— build: pin `maven-deploy-plugin` 3.1.3 in parent `pluginManagement` — fixes the missing plugin version warning on jbst-server-iam
— build: Maven Wrapper — `./mvnw` (Maven 3.9.9); all scripts, docs and CI switched from `mvn` to the wrapper
— refactor: docker conventions — `network-jbst` network, `volume-jbst-*` volumes, `jbst-database-*` service names, `restart: "no"` across all compose files
— fix: next-snapshot.sh — correct docker-compose paths (`files/docker` → `assets/docker`) so image tags get bumped again
— chore: add `ship-fast` Claude Code skill — ship without running the test suite (compile check only)
— chore: ship/ship-fast skills — local cleanup step (delete local branch, remove worktree folder, prune stale tracking ref) after merge
— build: `docker-build-locally.sh` — build the jbst-server-iam docker image locally, tagged from the workflow `DOCKER_VERSION`
— build: rename `delivery-check-fast.sh` → `push-check.sh` (unique autocomplete prefixes across root scripts)
— build: docker-compose image tags now track the current `-SNAPSHOT` version; `next-release.sh`/`next-snapshot.sh` keep them in sync with `DOCKER_VERSION`
— build: jbst-server-iam Dockerfile base `eclipse-temurin:17.0.7_7-jdk-alpine` → `17.0.16_8-jdk` (multi-arch, enables arm64/Apple Silicon local builds)
— refactor!: rename `hardcoded()` factory-method family to `fixed()` (incl. `fixedMasked()`, `fixedCurrent()`, `fixed*` email variants, `Domains.FIXED`, `TestJbstConfigurationPropertiesFixed`, `JbstFixedResource` with `/fixed` endpoint path) — breaking for library consumers using the old names
