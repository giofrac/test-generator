#!/bin/bash

# Setup script for developers
# Configures git hooks and local environment

set -e

echo "╔════════════════════════════════════════════════════════════╗"
echo "║  Test Generator - Developer Setup                          ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo

# Configure git
echo "Configuring git..."
git config core.hooksPath .githooks
chmod +x .githooks/pre-commit 2>/dev/null || true
echo "✓ Git hooks configured"
echo

# Build project
echo "Building project..."
./gradlew clean build -q
echo "✓ Build successful"
echo

# Run tests
echo "Running tests..."
./gradlew test -q
echo "✓ Tests passed"
echo

echo "Setup complete! You're ready to develop."
echo
echo "Quick commands:"
echo "  ./gradlew build    - Build project"
echo "  ./gradlew test     - Run tests"
echo "  ./testgen.sh       - Test the CLI"
echo "  ./demo.sh          - Run demo"
echo
echo "Before committing:"
echo "  1. Run tests: ./gradlew test"
echo "  2. Check code: ./gradlew clean build"
echo "  3. Format code if needed"
echo

