#!/bin/bash

EXTRACT_PATH="build/extracted"
APP_NAME="nl2sql-app"

# Color constants
CYAN='\033[0;36m'
RED='\033[0;31m'
GREEN='\033[0;32m'
PLAIN='\033[0m'

printf "${CYAN}--- [1/4] Building JAR locally.. ---${PLAIN}\n"
./gradlew bootJar -x test --parallel

if [ $? -ne 0 ]; then
    printf "${RED}--- Build Failed! Terminating.. ---${PLAIN}\n"
    exit 1
fi

printf "${CYAN}--- [2/4] Extracting Layers Locally.. ---${PLAIN}\n"
if [ -d "$EXTRACT_PATH" ]; then
    rm -rf "$EXTRACT_PATH"
fi
mkdir -p "$EXTRACT_PATH"

java -Djarmode=tools -jar "build/libs/app.jar" extract --layers --launcher --destination "$EXTRACT_PATH"

printf "${CYAN}--- [3/4] Docker Compose Build & Up.. ---${PLAIN}\n"
docker compose up -d app --build

printf "${GREEN}--- [4/4] Application is Starting.. ---${PLAIN}\n"
docker logs -f "${APP_NAME}"
