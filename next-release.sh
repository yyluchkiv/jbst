#!/usr/bin/env bash

PREFIX="[NextRelease]"
GITHUB_ACTION_MAIN_WORKFLOW=".github/workflows/main.yml"

echo "================================================================================================================="
echo "$PREFIX Maven versions started"

mvn versions:set -DremoveSnapshot -DgenerateBackupPoms=false

echo "$PREFIX Maven versions has been completed"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX GitHub Action, MAVEN_DEPLOYMENT_ENABLED started"

sed -i '' "s/MAVEN_DEPLOYMENT_ENABLED: .*/MAVEN_DEPLOYMENT_ENABLED: 'true'/" "$GITHUB_ACTION_MAIN_WORKFLOW"

echo "$PREFIX GitHub Action, MAVEN_DEPLOYMENT_ENABLED has been completed"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX GitHub Action, DOCKER_VERSION started"

sed -i '' "s/DOCKER_VERSION: '\(.*\)-SNAPSHOT'/DOCKER_VERSION: '\1'/" "$GITHUB_ACTION_MAIN_WORKFLOW"

echo "$PREFIX GitHub Action, DOCKER_VERSION has been completed"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX GitHub Action, DOCKER_PUSH_ENABLED started"

sed -i '' "s/DOCKER_PUSH_ENABLED: .*/DOCKER_PUSH_ENABLED: 'true'/" "$GITHUB_ACTION_MAIN_WORKFLOW"

echo "$PREFIX GitHub Action, DOCKER_PUSH_ENABLED has been completed"
echo "================================================================================================================="
