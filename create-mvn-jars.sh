#!/usr/bin/env bash

# Build foundation project
cd jbst-foundation || { echo "Folder jbst-foundation not found"; exit 1; }
mvn clean install -DskipTests
cd - || { echo "Parent folder not found"; exit 1; }

# Build iam project
cd jbst-iam || { echo "Folder jbst-iam not found"; exit 1; }
mvn clean install -DskipTests
cd - || { echo "Parent folder not found"; exit 1; }

# Check if the pom.xml file exists
POM_FILE="pom.xml"
if [ ! -f "$POM_FILE" ]; then
  echo "Error: pom.xml not found in the current directory!"
  exit 1
fi

# Extract the Maven project version
VERSION=$(grep -m 1 "<version>" "$POM_FILE" | sed -E 's/.*<version>([^<]+)<\/version>.*/\1/')

# Check if the version was found
if [ -n "$VERSION" ]; then
  # Make folder
  mkdir -p "mvn-jars"

  # Copy the foundation JAR file to mvn-jars
  cp "jbst-foundation/target/jbst-foundation-${VERSION}.jar" "mvn-jars/"

  # Copy the iam JAR file to mvn-jars
  cp "jbst-iam/target/jbst-iam-${VERSION}.jar" "mvn-jars/"

  # Copy the parent pom to mvn-jars
  cp "pom.xml" "mvn-jars/jbst-parent-pom.xml"

  echo "Files must be copied into mvn-jars"
else
  echo "Error: Could not extract project version!"
  exit 1
fi
