#!/usr/bin/env bash

mvn clean install -DskipTests
mvn failsafe:integration-test

