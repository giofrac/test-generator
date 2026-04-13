#!/bin/bash

# Quick Start Guide - Interactive Setup

echo "╔════════════════════════════════════════════════════════════╗"
echo "║  Test Generator - Quick Start                              ║"
echo "║  Version 1.0.0                                             ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo

if [ ! -f "build/libs/IntelliJ_Platform_Plugin-1.0.0.jar" ]; then
    echo "Step 1: Building the project (first time only)..."
    ./gradlew build -q
    echo "✓ Build complete"
    echo
fi

echo "Usage Options:"
echo
echo "1. Show help:"
echo "   ./testgen.sh --help"
echo
echo "2. Generate tests for all methods:"
echo "   ./testgen.sh <sourcefile.java>"
echo
echo "3. Generate test for specific method:"
echo "   ./testgen.sh <sourcefile.java> --method <methodname>"
echo
echo "4. Custom output directory:"
echo "   ./testgen.sh <sourcefile.java> --output <directory>"
echo
echo "5. View demo:"
echo "   ./demo.sh"
echo
echo "6. Run tests:"
echo "   ./gradlew test"
echo
echo "Example (with project files):"
echo "   ./testgen.sh src/main/java/com/giofrac/Calculator.java --method somma"
echo
echo "────────────────────────────────────────────────────────────"
echo "For more details, see: README.md"
echo "To contribute, see: CONTRIBUTING.md"
echo "════════════════════════════════════════════════════════════"

