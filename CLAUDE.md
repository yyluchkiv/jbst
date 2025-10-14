# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

JBST is a Java 17 Spring Boot framework providing bootstrapping tools for enterprise applications. It's structured as a multi-module Maven project with the following key modules:

- **jbst-foundation**: Core domain objects, utilities, configurations, and foundation services
- **jbst-iam**: Identity and Access Management module with JWT-based security
- **jbst-server-iam**: IAM server application supporting both MongoDB and PostgreSQL
- **jbst-server-hardware-monitoring**: Hardware monitoring server application
- **jbst-server-ops**: Operations server for incident management

## Build and Development Commands

### Core Maven Commands
```bash
# Compile only (no tests)
./compile-all.sh  # or mvn clean compile test-compile

# Unit tests only
./junit-tests-only.sh  # or mvn clean test

# Integration tests only
./integrations-tests-only.sh  # or mvn failsafe:integration-test

# All tests (unit + integration)
mvn integration-test

# Complete verification
mvn clean verify

# Quick build without tests
./delivery-check-fast.sh  # or mvn clean install -Dmaven.test.skip -DskipTests -T 4
```

### Database Support
The IAM module supports both MongoDB and PostgreSQL. Use the appropriate profile:
```bash
# Run with MongoDB
./jbst-server-iam/run-mongodb.sh

# Run with PostgreSQL  
./jbst-server-iam/run-postgres.sh

# Docker setup
./docker/run-mongodb.sh
./docker/run-postgres.sh
```

### Code Quality
```bash
# SonarQube analysis
./sonar-check.sh

# Pre-push validation
./push-check.sh
```

## Architecture

### Module Dependencies
- `jbst-foundation`: Base module containing domain objects, utilities, and configurations
- `jbst-iam`: Depends on foundation, provides security and user management
- Server modules: Depend on their respective JAR modules for runtime execution

### Key Configuration
- **Properties**: All configuration is centralized through `JbstProperties` class with prefix `jbst:`
- **Security**: JWT-based authentication with configurable authorities and WebSocket support
- **Database**: Dual database support (MongoDB/PostgreSQL) with Liquibase migrations for PostgreSQL
- **Monitoring**: Built-in hardware monitoring with configurable thresholds

### Package Structure
```
jbst.foundation/
├── configurations/     # Spring configurations
├── domain/            # Core domain objects and entities
├── utilities/         # Utility classes
├── services/          # Foundation services
└── incidents/         # Incident management

jbst.iam/
├── configurations/    # Security configurations  
├── domain/           # IAM-specific entities
├── services/         # Authentication/authorization services
├── repositories/     # Data access layer
└── resources/        # REST endpoints
```

## Testing Strategy

### Test Structure
- **Unit tests**: `src/test/java` - Fast tests using mocks
- **Integration tests**: `src/test-integration/java` - Database integration with Testcontainers
- **Test separation**: Maven Surefire (unit) and Failsafe (integration) plugins

### Database Testing
Integration tests use Testcontainers for both MongoDB and PostgreSQL, ensuring database compatibility.

## Development Notes

### Lombok Integration
- Custom `lombok.config` with field name `LOGGER` for logging
- All classes use Lombok annotations for reduced boilerplate

### Code Quality
- JaCoCo for code coverage reporting
- SonarQube integration with extensive exclusion patterns
- Comprehensive Maven plugin setup for quality gates

### Profiles and Environments
- Development profiles: `application-dev.yml`
- Database-specific configs: `application-mongo.yml`, `application-postgres.yml`
- Docker configurations available in `/docker` directory

## Server Applications

Each server module is a standalone Spring Boot application:
- **Port configuration**: Via application properties
- **Context path**: `/api` for all applications
- **Swagger UI**: Available at `{server}/api/swagger-ui/index.html`
- **Health checks**: Spring Boot Actuator endpoints enabled
