### Changelog [v1.70]
— build: Maven Wrapper — `./mvnw` (Maven 3.9.9); all scripts, docs and CI switched from `mvn` to the wrapper
— refactor: docker conventions — `network-jbst` network, `volume-jbst-*` volumes, `jbst-database-*` service names, `restart: "no"` across all compose files
— fix: next-snapshot.sh — correct docker-compose paths (`files/docker` → `assets/docker`) so image tags get bumped again
— chore: add `ship-fast` Claude Code skill — ship without running the test suite (compile check only)
