#!/usr/bin/env bash

PREFIX="[NextSnapshot]"
GITHUB_ACTION_MAIN_WORKFLOW=".github/workflows/main.yml"
CHANGELOG_PATH="CHANGELOG.md"
DOCKER_COMPOSE_MONGO_PATH="assets/docker/docker-compose.mongo.yml"
DOCKER_COMPOSE_POSTGRES_PATH="assets/docker/docker-compose.postgres.yml"

MAJOR_VERSION_NUMBER=$(grep "DOCKER_VERSION:" "$GITHUB_ACTION_MAIN_WORKFLOW" | grep -oE '[0-9]+\.[0-9]+' | awk -F '[.-]' '{print $1}')
MINOR_VERSION_NUMBER=$(grep "DOCKER_VERSION:" "$GITHUB_ACTION_MAIN_WORKFLOW" | grep -oE '[0-9]+\.[0-9]+' | awk -F '[.-]' '{print $2}')
((MINOR_VERSION_NUMBER++))
NEXT_RELEASE_CHANGELOG_VERSION="[v$MAJOR_VERSION_NUMBER.$MINOR_VERSION_NUMBER]"
NEXT_SNAPSHOT_VERSION="$MAJOR_VERSION_NUMBER.$MINOR_VERSION_NUMBER-SNAPSHOT"
NEXT_SNAPSHOT_DOCKER_VERSION="'$NEXT_SNAPSHOT_VERSION'"

echo "================================================================================================================="
echo "$PREFIX Maven versions started"

./mvnw versions:set -DnextSnapshot=true -DgenerateBackupPoms=false

echo "$PREFIX Maven versions has been completed"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX GitHub Action, MAVEN_DEPLOYMENT_ENABLED started"

sed -i '' "s/MAVEN_DEPLOYMENT_ENABLED: .*/MAVEN_DEPLOYMENT_ENABLED: 'false'/" "$GITHUB_ACTION_MAIN_WORKFLOW"

echo "$PREFIX GitHub Action, MAVEN_DEPLOYMENT_ENABLED has been completed"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX GitHub Action, DOCKER_VERSION started"

sed -i '' "s/DOCKER_VERSION: .*/DOCKER_VERSION: $NEXT_SNAPSHOT_DOCKER_VERSION/" "$GITHUB_ACTION_MAIN_WORKFLOW"

echo "$PREFIX GitHub Action, DOCKER_VERSION has been completed"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX GitHub Action, DOCKER_PUSH_ENABLED started"

sed -i '' "s/DOCKER_PUSH_ENABLED: .*/DOCKER_PUSH_ENABLED: 'false'/" "$GITHUB_ACTION_MAIN_WORKFLOW"

echo "$PREFIX GitHub Action, DOCKER_PUSH_ENABLED has been completed"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX CHANGELOG started"

echo -e "### Changelog $NEXT_RELEASE_CHANGELOG_VERSION\n— TBD" > "$CHANGELOG_PATH"

echo "$PREFIX CHANGELOG has been completed"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX docker-compose started"

sed -i '' "s|image: ghcr.io/yyluchkiv/jbst-server-iam:.*|image: ghcr.io/yyluchkiv/jbst-server-iam:$NEXT_SNAPSHOT_VERSION|" "$DOCKER_COMPOSE_MONGO_PATH"
sed -i '' "s|image: ghcr.io/yyluchkiv/jbst-server-iam:.*|image: ghcr.io/yyluchkiv/jbst-server-iam:$NEXT_SNAPSHOT_VERSION|" "$DOCKER_COMPOSE_POSTGRES_PATH"

echo "$PREFIX docker-compose has been completed"
echo "================================================================================================================="

