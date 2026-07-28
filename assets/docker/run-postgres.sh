#!/usr/bin/env bash

docker-compose -f "$(pwd)"/docker-compose.postgres.yml pull
#docker-compose -f "$(pwd)"/docker-compose.postgres.yml up -d
docker-compose -f "$(pwd)"/docker-compose.postgres.yml up
