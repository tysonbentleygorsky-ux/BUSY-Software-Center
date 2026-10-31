#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
mvn clean package
mkdir -p dist
jpackage --type dmg --name "BUSY School Software Center" --app-version "2.0.0" --input target --main-jar busy-desktop-2.0.0.jar --dest dist --vendor "BUSY School Software Center"
