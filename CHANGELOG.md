### Changelog [v1.71]
— build!: Java 17 → 21 (POM `version.java`, CI workflow, Docker base image `eclipse-temurin:21.0.11_10-jdk`, Sonar); breaking — consumers must run JDK 21+
— build: force project lombok (1.18.36) as `lombok-maven-plugin` dependency — plugin-bundled 1.18.20 cannot delombok on JDK 21
— build: bump Testcontainers 1.20.4 → 1.21.4 — older docker-java calls Docker Engine API v1.32, rejected by current Docker Desktop (MinAPIVersion 1.40)
