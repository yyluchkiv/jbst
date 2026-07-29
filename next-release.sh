#!/usr/bin/env bash

PREFIX="[NextRelease]"
# Deploy flags/version live in a plain env file, NOT in .github/workflows/main.yml —
# the release workflow's GITHUB_TOKEN cannot push commits that modify workflow files.
DEPLOYMENT_ENV_FILE=".github/deployment.env"
DOCKER_COMPOSE_MONGO_PATH="assets/docker/docker-compose.mongo.yml"
DOCKER_COMPOSE_POSTGRES_PATH="assets/docker/docker-compose.postgres.yml"

# In-place sed differs between macOS and Linux: BSD sed (macOS) requires a backup-suffix
# argument after -i ('' = no backup file), GNU sed (Linux, used by the release.yml runner)
# treats that '' as a filename and fails — so it must be plain -i there.
if [[ "$OSTYPE" == darwin* ]]; then
    SED_INPLACE=(sed -i '')
else
    SED_INPLACE=(sed -i)
fi

echo "================================================================================================================="
echo "$PREFIX Maven versions started"

./mvnw versions:set -DremoveSnapshot -DgenerateBackupPoms=false

echo "$PREFIX Maven versions has been completed"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX deployment.env, MAVEN_DEPLOYMENT_ENABLED started"

"${SED_INPLACE[@]}" "s/MAVEN_DEPLOYMENT_ENABLED=.*/MAVEN_DEPLOYMENT_ENABLED=true/" "$DEPLOYMENT_ENV_FILE"

echo "$PREFIX deployment.env, MAVEN_DEPLOYMENT_ENABLED has been completed"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX deployment.env, DOCKER_VERSION started"

"${SED_INPLACE[@]}" "s/DOCKER_VERSION=\(.*\)-SNAPSHOT/DOCKER_VERSION=\1/" "$DEPLOYMENT_ENV_FILE"

echo "$PREFIX deployment.env, DOCKER_VERSION has been completed"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX deployment.env, DOCKER_PUSH_ENABLED started"

"${SED_INPLACE[@]}" "s/DOCKER_PUSH_ENABLED=.*/DOCKER_PUSH_ENABLED=true/" "$DEPLOYMENT_ENV_FILE"

echo "$PREFIX deployment.env, DOCKER_PUSH_ENABLED has been completed"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX docker-compose started"

"${SED_INPLACE[@]}" "s|\(image: ghcr.io/yyluchkiv/jbst-server-iam:.*\)-SNAPSHOT|\1|" "$DOCKER_COMPOSE_MONGO_PATH"
"${SED_INPLACE[@]}" "s|\(image: ghcr.io/yyluchkiv/jbst-server-iam:.*\)-SNAPSHOT|\1|" "$DOCKER_COMPOSE_POSTGRES_PATH"

echo "$PREFIX docker-compose has been completed"
echo "================================================================================================================="
