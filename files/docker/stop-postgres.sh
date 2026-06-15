#!/usr/bin/env bash

docker-compose -f "$(pwd)"/docker-compose.postgres.yml down --volumes
