#!/usr/bin/env bash

docker-compose -f "$(pwd)"/docker-compose.mongo.yml down --volumes
