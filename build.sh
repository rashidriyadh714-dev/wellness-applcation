#!/bin/bash
# Elite Wellness Build Script
# Compiles all Java sources and packages the application.

set -euo pipefail

echo "Building Elite Wellness application"
echo "=================================="

if ! command -v javac >/dev/null 2>&1; then
    echo "ERROR: javac not found. Install a JDK and retry."
    exit 1
fi

if ! command -v jar >/dev/null 2>&1; then
    echo "ERROR: jar tool not found. Install a JDK and retry."
    exit 1
fi

mkdir -p out dist

echo "Compiling Java sources..."
find src -name "*.java" -print0 | xargs -0 javac -d out

if [ ! -f "out/wellness/Main.class" ]; then
    echo "ERROR: expected out/wellness/Main.class was not generated."
    exit 1
fi

echo "Creating runnable JAR..."
jar cfe dist/EliteWellness.jar wellness.Main -C out .

echo "Build complete"
echo "Output: dist/EliteWellness.jar"
echo "Run with: java -jar dist/EliteWellness.jar"
