#!/bin/bash

# Test Generator CLI Wrapper
# Simplifies running the test generator from command line

VERSION="1.0.0"
JAR_FILE="$(dirname "$0")/build/libs/IntelliJ_Platform_Plugin-1.0.0.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found at $JAR_FILE"
    echo "Please run: ./gradlew build"
    exit 1
fi

# Show help
if [ "$1" = "--help" ] || [ "$1" = "-h" ] || [ -z "$1" ]; then
    java -cp "$JAR_FILE" com.giofrac.TestGeneratorCLI --help
    exit 0
fi

# Pass all arguments to the Java class
java -cp "$JAR_FILE" com.giofrac.TestGeneratorCLI "$@"

