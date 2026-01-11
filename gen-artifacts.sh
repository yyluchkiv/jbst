#!/usr/bin/env bash

# print/colors
GREEN='\033[0;32m'
RED='\033[0;31m'
NO_COLOR='\033[0m'

print() { echo -e "${1}${2}${NO_COLOR}"; }

# TODO [YYL-scripts] make this script faster
# jbst
mvn clean install -DskipTests

# jbst-foundation
cd jbst-foundation || { print "${RED}" "[ERROR]: Folder jbst-foundation not found"; exit 1; }
mvn clean install -DskipTests
cd - || { print "${RED}" "[ERROR]: Parent folder not found"; exit 1; }

# jbst-parent-pom.xml
POM_FILE="pom.xml"
if [ ! -f "$POM_FILE" ]; then
  print "${RED}" "[ERROR]: parent pom.xml not found"
  exit 1
fi

# <version></version>
VERSION=$(grep -m 1 "<version>" "$POM_FILE" | sed -E 's/.*<version>([^<]+)<\/version>.*/\1/')

if [ -n "$VERSION" ]; then
  mkdir -p "artifacts"
  cp "jbst-foundation/target/jbst-foundation-${VERSION}.jar" "artifacts/"
  cp "pom.xml" "artifacts/jbst-parent-pom.xml"
  print "${GREEN}" "------------------------------------------------------------------------"
  print "${GREEN}" "[SUCCESS] jbst artifacts completed"
  print "${GREEN}" "------------------------------------------------------------------------"
else
  print "${RED}" "------------------------------------------------------------------------"
  print "${RED}" "[ERROR]: jbst version extraction failure"
  print "${RED}" "------------------------------------------------------------------------"
  exit 1
fi
