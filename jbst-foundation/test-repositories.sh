#!/usr/bin/env bash

cd "$(dirname "$0")" || exit 1

../mvnw clean install -DskipTests
../mvnw failsafe:integration-test
