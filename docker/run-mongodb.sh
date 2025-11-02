#!/usr/bin/env bash

docker-compose -f "$(pwd)"/docker-compose.mongo.yml pull
#docker-compose -f "$(pwd)"/docker-compose.mongo.yml up -d
docker-compose -f "$(pwd)"/docker-compose.mongo.yml up
