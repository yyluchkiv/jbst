#!/usr/bin/env bash

./mvnw -pl 'jbst-foundation' clean install -DskipTests -T 4 -am
