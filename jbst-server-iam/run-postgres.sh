#!/usr/bin/env bash
# shellcheck disable=SC2046

# print/colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NO_COLOR='\033[0m'

print() { echo -e "${1}${2}${NO_COLOR}"; }

# java

METHOD=maven
PORT=3002
SPRING_BOOT_PROFILE=postgres
SPRING_BOOT_CONFIG_LOCATION=classpath:application.yml,classpath:application-dev.yml,classpath:application-postgres.yml,classpath:application-postgres-dev.yml
JASYPT_PASSWORD=JJJJBSTGH
JVM_ARGUMENTS="-Xms512m -Xmx2g --add-opens=java.base/java.time=ALL-UNNAMED --add-opens=java.base/java.math=ALL-UNNAMED"

print "================================================================================================================="
print "${BLUE}" "PostgreSQL 'jbst': STARTED"
print "================================================================================================================="

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
docker compose -f "$SCRIPT_DIR/docker/postgres/docker-compose.yml" up -d

until docker exec jbst-database-postgres pg_isready -U postgres -q; do
  print "${BLUE}" "PostgreSQL 'jbst': waiting for readiness..."
  sleep 1
done

docker exec jbst-database-postgres psql -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname = 'jbst'" | grep -q 1 \
  || docker exec jbst-database-postgres psql -U postgres -c "CREATE DATABASE jbst"

print "================================================================================================================="
print "${GREEN}" "PostgreSQL 'jbst': COMPLETED"
print "================================================================================================================="

java-run-spring-boot-dev-profile-v4.sh $METHOD $PORT "$SPRING_BOOT_PROFILE" $SPRING_BOOT_CONFIG_LOCATION $JASYPT_PASSWORD "$JVM_ARGUMENTS"
