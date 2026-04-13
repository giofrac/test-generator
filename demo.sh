#!/bin/bash

# Quick Demo Script
# Shows the capabilities of Test Generator

set -e

echo "╔════════════════════════════════════════════════════════════╗"
echo "║         Test Generator - MVP Demo                          ║"
echo "║  Automatically generates JUnit 5 test skeletons           ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo

# Build
echo "[1/4] Building project..."
./gradlew clean build -q
echo "✓ Build successful"
echo

# Show Calculator class
echo "[2/4] Source code (Calculator.java):"
echo "─────────────────────────────────────"
head -15 src/main/java/com/giofrac/Calculator.java
echo "..."
echo

# Generate test
echo "[3/4] Generating test for 'somma' method..."
echo "─────────────────────────────────────────"
rm -rf /tmp/testgen_demo
./testgen.sh src/main/java/com/giofrac/Calculator.java --method somma --output /tmp/testgen_demo
echo

# Show generated test
echo "[4/4] Generated test code (CalculatorTest.java):"
echo "──────────────────────────────────────────────"
cat /tmp/testgen_demo/CalculatorTest.java
echo

echo "╔════════════════════════════════════════════════════════════╗"
echo "║  Demo Complete!                                            ║"
echo "║                                                            ║"
echo "║  Next Steps:                                               ║"
echo "║  1. Run tests: ./gradlew test                              ║"
echo "║  2. Generate all methods: ./testgen.sh Calculator.java     ║"
echo "║  3. Read README.md for full documentation                 ║"
echo "╚════════════════════════════════════════════════════════════╝"

