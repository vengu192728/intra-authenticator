#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JAR="${ROOT}/target/intra-authenticator-1.0.0.jar"
CREDENTIALS_FILE="${AUTH_CREDENTIALS_FILE:-${ROOT}/config/credentials.txt}"

if [[ ! -f "${JAR}" ]]; then
  echo "JAR not found. Run: mvn -f \"${ROOT}/pom.xml\" clean package -DskipTests"
  exit 1
fi

if [[ ! -f "${CREDENTIALS_FILE}" ]]; then
  echo "Credentials file not found: ${CREDENTIALS_FILE}"
  echo "Copy ${ROOT}/config/credentials.example.txt to credentials.txt and add accounts."
  exit 1
fi

export AUTH_CREDENTIALS_FILE="${CREDENTIALS_FILE}"
exec java -jar "${JAR}"
