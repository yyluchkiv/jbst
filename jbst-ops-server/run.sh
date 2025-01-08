#!/usr/bin/env bash

METHOD=maven
PORT=8973
SPRING_BOOT_PROFILE=
SPRING_BOOT_CONFIG_LOCATION=classpath:application.yml,classpath:application-dev.yml
JASYPT_PASSWORD=abcde1234567890
JVM_ARGUMENTS="-Xms512m -Xmx1g --add-opens=java.base/java.time=ALL-UNNAMED"

java-run-spring-boot-dev-profile-v4.sh $METHOD $PORT "$SPRING_BOOT_PROFILE" $SPRING_BOOT_CONFIG_LOCATION $JASYPT_PASSWORD "$JVM_ARGUMENTS"
