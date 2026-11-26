#!/bin/sh
set -eu

PROJECT_DIR=$(CDPATH= cd "$(dirname "$0")" && pwd)
WRAPPER_PROPERTIES="$PROJECT_DIR/gradle/wrapper/gradle-wrapper.properties"
DIST_URL=$(sed -n 's/^distributionUrl=//p' "$WRAPPER_PROPERTIES" | sed 's/\\:/:/g')
ARCHIVE=${DIST_URL##*/}
DIST_NAME=${ARCHIVE%-bin.zip}
GRADLE_USER_HOME=${GRADLE_USER_HOME:-$PROJECT_DIR/.gradle}
DIST_DIR="$GRADLE_USER_HOME/wrapper/dists/$DIST_NAME"
GRADLE_HOME="$DIST_DIR/$DIST_NAME"

if [ ! -x "$GRADLE_HOME/bin/gradle" ]; then
  mkdir -p "$DIST_DIR"
  TEMP_DIR="$DIST_DIR/.install-$$"
  mkdir -p "$TEMP_DIR"
  trap 'rm -rf "$TEMP_DIR"' EXIT HUP INT TERM
  echo "Downloading Gradle from $DIST_URL"
  if ! curl --fail --location --retry 2 --output "$TEMP_DIR/$ARCHIVE" "$DIST_URL"; then
    echo "Could not download Gradle. Check network access and retry ./gradlew." >&2
    exit 1
  fi
  unzip -q "$TEMP_DIR/$ARCHIVE" -d "$TEMP_DIR"
  mv "$TEMP_DIR/$DIST_NAME" "$GRADLE_HOME"
  rm -rf "$TEMP_DIR"
  trap - EXIT HUP INT TERM
fi

exec "$GRADLE_HOME/bin/gradle" "$@"
