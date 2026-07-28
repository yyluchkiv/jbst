#!/usr/bin/env bash

./mvnw clean install -Dmaven.test.skip -DskipTests -T 4
