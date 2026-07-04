#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
OUT="$ROOT/ext-example/build"
API="$ROOT/app/src/main/java"
ANDROID_JAR="${ANDROID_HOME:-/home/ubuntu/android-sdk}/platforms/android-36/android.jar"
D8="${ANDROID_HOME:-/home/ubuntu/android-sdk}/build-tools/36.0.0/d8"

rm -rf "$OUT"
mkdir -p "$OUT/classes"

ANNOTATIONS="/home/ubuntu/.gradle/caches/modules-2/files-2.1/org.jetbrains/annotations/23.0.0/8cc20c07506ec18e0834947b84a864bfc094484e/annotations-23.0.0.jar"

javac --release 17 \
  -classpath "$ANDROID_JAR:$API:$ANNOTATIONS" \
  -d "$OUT/classes" \
  "$ROOT/ext-example/src/TarnhelmExt.java" \
  "$ROOT/ext-example/src/ExampleExtService.java"

mkdir -p "$OUT/dex"
"$D8" --output "$OUT/dex" $(find "$OUT/classes" -name '*.class')

cp "$OUT/dex/classes.dex" "$ROOT/app/src/main/assets/extensions/example.dex"
cp "$OUT/dex/classes.dex" "$ROOT/ext-example/example.dex"
echo "Built $ROOT/app/src/main/assets/extensions/example.dex"
