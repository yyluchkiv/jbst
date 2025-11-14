#!/usr/bin/env bash

OWNER=tech1-agency
REPO=jbst
GROUP_ID=jbst
ARTIFACT_ID=jbst-server-hardware-monitoring
FOLDER="/root/artifacts"

VERSION_DEFAULT=1.37

read -p "Provide jbst-server-hardware-monitoring version or press enter for default version '$VERSION_DEFAULT': " VERSION_USER_INPUT
read -p "Provide GitHub Access Token: " GITHUB_TOKEN_USER_INPUT

VERSION=${VERSION_USER_INPUT:-$VERSION_DEFAULT}

echo "==========================================================================================="
echo "Parameter: VERSION = $VERSION"
echo "Parameter: GITHUB_TOKEN = $GITHUB_TOKEN_USER_INPUT"
echo "==========================================================================================="

if [ -z "$GITHUB_TOKEN_USER_INPUT" ]; then
  echo -e "\033[31m==========================================================================================="
  echo -e "GitHub access token is required. Exiting."
  echo -e "===========================================================================================\033[0m"
  exit
fi

if [ ! -d "$FOLDER" ]; then
    mkdir -p "$FOLDER"
fi

[ -f application-hm-server.yml ] && mv application-hm-server.yml $FOLDER

cd $FOLDER || exit

curl -H "Authorization: token $GITHUB_TOKEN_USER_INPUT" -L -o hms.jar \
  https://maven.pkg.github.com/$OWNER/$REPO/packages/$GROUP_ID/$ARTIFACT_ID/"$VERSION"/$ARTIFACT_ID-"$VERSION".jar

curl -H "Authorization: token $GITHUB_TOKEN_USER_INPUT" -H "Accept: application/vnd.github.v3.raw" -O -L \
  https://raw.githubusercontent.com/$OWNER/devkit/main/installations/jbst-hm-server/hms.service

mv hms.service /etc/systemd/system
systemctl daemon-reload
systemctl restart hms.service
systemctl status hms.service

