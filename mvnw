#!/bin/sh
set -eu

PROJECT_DIR=$(CDPATH= cd "$(dirname "$0")" && pwd)
WRAPPER_PROPERTIES="$PROJECT_DIR/.mvn/wrapper/maven-wrapper.properties"
if [ ! -f "$WRAPPER_PROPERTIES" ]; then
  echo "Maven Wrapper config not found: $WRAPPER_PROPERTIES" >&2
  exit 1
fi

DIST_URL=$(sed -n 's/^distributionUrl=//p' "$WRAPPER_PROPERTIES" | head -n 1)
if [ -z "$DIST_URL" ]; then
  echo "distributionUrl is missing from $WRAPPER_PROPERTIES" >&2
  exit 1
fi

ARCHIVE=${DIST_URL##*/}
DIST_NAME=${ARCHIVE%-bin.tar.gz}
MAVEN_USER_HOME=${MAVEN_USER_HOME:-$PROJECT_DIR/.mvn/cache}
DIST_DIR="$MAVEN_USER_HOME/wrapper/dists/$DIST_NAME"
MAVEN_HOME="$DIST_DIR/$DIST_NAME"

if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
  mkdir -p "$DIST_DIR"
  TEMP_DIR="$DIST_DIR/.install-$$"
  mkdir -p "$TEMP_DIR"
  trap 'rm -rf "$TEMP_DIR"' EXIT HUP INT TERM
  echo "Downloading Maven from $DIST_URL"
  if ! curl --fail --location --retry 2 --output "$TEMP_DIR/$ARCHIVE" "$DIST_URL"; then
    echo "Could not download Maven. Check network access and retry ./mvnw." >&2
    exit 1
  fi
  tar -xzf "$TEMP_DIR/$ARCHIVE" -C "$TEMP_DIR"
  mv "$TEMP_DIR/$DIST_NAME" "$MAVEN_HOME"
  rm -rf "$TEMP_DIR"
  trap - EXIT HUP INT TERM
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
