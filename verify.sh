#!/bin/bash
# Elite Wellness Verification Script
# Performs a clean compile and runs smoke tests.

set -euo pipefail

echo "Verifying Elite Wellness"
echo "========================"

rm -rf out
mkdir -p out

find src -name "*.java" -print0 | xargs -0 javac -d out

echo "Running smoke tests..."
java -cp out wellness.test.AnalyticsPipelineTest
java -cp out wellness.test.TelemetryTest

echo "Verification passed"
