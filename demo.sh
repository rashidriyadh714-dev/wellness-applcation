#!/bin/bash
# Elite Wellness Demo Script
# One-command flow for presentation day.

set -euo pipefail

echo "Elite Wellness Demo Prep"
echo "========================"

bash verify.sh
bash build.sh

echo "Starting app..."
echo "Tip: In login, click 'Continue Demo' for instant entry."
java -jar dist/EliteWellness.jar
