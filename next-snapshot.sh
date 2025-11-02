#!/usr/bin/env bash

PREFIX="[NextSnapshot]"
GITHUB_ACTION_MAIN_WORKFLOW=".github/workflows/main.yml"
CHANGELOG_PATH="CHANGELOG.md"
DOCKER_COMPOSE_MONGODB_PATH="docker/docker-compose.mongo.yml"
DOCKER_COMPOSE_POSTGRES_PATH="docker/docker-compose.postgres.yml"

MAJOR_VERSION_NUMBER=$(grep "DOCKER_VERSION:" "$GITHUB_ACTION_MAIN_WORKFLOW" | grep -oE '[0-9]+\.[0-9]+' | awk -F '[.-]' '{print $1}')
MINOR_VERSION_NUMBER=$(grep "DOCKER_VERSION:" "$GITHUB_ACTION_MAIN_WORKFLOW" | grep -oE '[0-9]+\.[0-9]+' | awk -F '[.-]' '{print $2}')
CURRENT_RELEASE_CHANGELOG_VERSION="$MAJOR_VERSION_NUMBER.$MINOR_VERSION_NUMBER"
((MINOR_VERSION_NUMBER++))
NEXT_RELEASE_CHANGELOG_VERSION="[v$MAJOR_VERSION_NUMBER.$MINOR_VERSION_NUMBER]"
NEXT_SNAPSHOT_DOCKER_VERSION="'$MAJOR_VERSION_NUMBER.$MINOR_VERSION_NUMBER-SNAPSHOT'"

echo "================================================================================================================="
echo "$PREFIX Maven versions started"

mvn versions:set -DnextSnapshot=true -DgenerateBackupPoms=false

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

CURRENT_RELEASE_IMAGE="    image: ghcr.io\/tech1-agency\/jbst-server-iam:$CURRENT_RELEASE_CHANGELOG_VERSION"

sed -i '' '3s/.*/'"$CURRENT_RELEASE_IMAGE"'/' "$DOCKER_COMPOSE_MONGODB_PATH"
sed -i '' '3s/.*/'"$CURRENT_RELEASE_IMAGE"'/' "$DOCKER_COMPOSE_POSTGRES_PATH"

echo "$PREFIX docker-compose has been completed"
echo "================================================================================================================="

