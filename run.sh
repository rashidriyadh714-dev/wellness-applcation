#!/bin/bash
# Elite Wellness Run Script
# Builds if needed, then launches the desktop app.

set -euo pipefail

echo "Launching Elite Wellness"
echo "========================"

if [ ! -f "dist/EliteWellness.jar" ]; then
    echo "JAR not found. Building first..."
    bash build.sh
fi

java -jar dist/EliteWellness.jar
