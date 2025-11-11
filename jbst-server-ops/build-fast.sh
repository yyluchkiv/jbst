#!/usr/bin/env bash

mvn -pl 'jbst-foundation' clean install -DskipTests -T 4 -am
