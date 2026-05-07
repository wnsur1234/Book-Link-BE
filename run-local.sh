#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$PROJECT_ROOT/.env"

if [[ ! -f "$ENV_FILE" ]]; then
  echo ".env 파일을 찾을 수 없습니다: $ENV_FILE" >&2
  exit 1
fi

echo "Loading environment variables from .env ..."
while IFS= read -r line || [[ -n "$line" ]]; do
  line="${line%$'\r'}"
  [[ -z "$line" || "${line:0:1}" == "#" ]] && continue
  [[ "$line" != *"="* ]] && continue

  key="${line%%=*}"
  value="${line#*=}"

  # Trim key spaces
  key="${key#"${key%%[![:space:]]*}"}"
  key="${key%"${key##*[![:space:]]}"}"

  # Remove optional surrounding quotes
  if [[ "${value:0:1}" == "\"" && "${value: -1}" == "\"" ]]; then
    value="${value:1:${#value}-2}"
  elif [[ "${value:0:1}" == "'" && "${value: -1}" == "'" ]]; then
    value="${value:1:${#value}-2}"
  fi

  export "$key=$value"
done < "$ENV_FILE"
echo "Environment variables loaded."

if [[ "${1:-}" == "--skip-run" ]]; then
  echo "--skip-run 옵션으로 실행을 건너뜁니다."
  exit 0
fi

echo "Starting Spring Boot with Gradle..."
cd "$PROJECT_ROOT"
./gradlew bootRun
