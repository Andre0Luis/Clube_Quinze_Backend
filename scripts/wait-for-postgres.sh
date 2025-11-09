#!/usr/bin/env bash
# Wait for a TCP host:port to become available
# usage: wait-for-postgres.sh host port [timeout-seconds]
set -eu
host=${1:-localhost}
port=${2:-5432}
timeout=${3:-120}

echo "Waiting for $host:$port (timeout ${timeout}s)..."
start_time=$(date +%s)
while true; do
  # try /dev/tcp (bash built-in)
  if bash -c "</dev/tcp/${host}/${port}" >/dev/null 2>&1; then
    echo "$host:$port is available (via /dev/tcp)"
    break
  fi
  # fallback to nc if available
  if command -v nc >/dev/null 2>&1; then
    if nc -z "$host" "$port" >/dev/null 2>&1; then
      echo "$host:$port is available (via nc)"
      break
    fi
  fi

  now=$(date +%s)
  elapsed=$((now - start_time))
  if [ "$elapsed" -ge "$timeout" ]; then
    echo "Timed out waiting for $host:$port after ${timeout}s" >&2
    exit 1
  fi
  sleep 1
done
