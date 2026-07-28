#!/usr/bin/env bash

PREFIX="[DockerBuildLocally]"
GITHUB_ACTION_MAIN_WORKFLOW=".github/workflows/main.yml"

DOCKER_REGISTRY=$(grep "DOCKER_REGISTRY:" "$GITHUB_ACTION_MAIN_WORKFLOW" | awk '{print $2}' | tr -d "'")
DOCKER_IMAGE_SERVER_IAM=$(grep "DOCKER_IMAGE_SERVER_IAM:" "$GITHUB_ACTION_MAIN_WORKFLOW" | awk '{print $2}' | tr -d "'")
DOCKER_VERSION=$(grep "DOCKER_VERSION:" "$GITHUB_ACTION_MAIN_WORKFLOW" | awk '{print $2}' | tr -d "'")
DOCKER_IMAGE="$DOCKER_REGISTRY/$DOCKER_IMAGE_SERVER_IAM:$DOCKER_VERSION"

echo "================================================================================================================="
echo "$PREFIX Maven build started"

./mvnw clean install -Dmaven.test.skip -DskipTests -T 4 || exit 1

echo "$PREFIX Maven build has been completed"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX Docker build started: $DOCKER_IMAGE"

docker build -t "$DOCKER_IMAGE" jbst-server-iam || exit 1

echo "$PREFIX Docker build has been completed: $DOCKER_IMAGE"
echo "================================================================================================================="
