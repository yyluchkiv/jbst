#!/usr/bin/env bash

# print/colors
GREEN='\033[0;32m'
RED='\033[0;31m'
NO_COLOR='\033[0m'

print() { echo -e "${1}${2}${NO_COLOR}"; }

if [ -z "$(lsof -i :9000)" ]; then
    print "------------------------------------------------------------------------------"
    print "${RED}" "SonarQube check execution: FAILURE"
    print "------------------------------------------------------------------------------"
else
    # Run install
    mvn clean install
    # Run sonar scanner
    sonar-scanner
    if [ "$?" -ne "0" ]; then
        print "------------------------------------------------------------------------------"
        print "${RED}" "SonarQube check execution: FAILURE"
        print "------------------------------------------------------------------------------"
    else
        print "------------------------------------------------------------------------------"
        print "${GREEN}" "SonarQube check execution: SUCCESS"
        print "------------------------------------------------------------------------------"
    fi
fi
