@echo off

REM Test Generator CLI Wrapper for Windows
REM Simplifies running the test generator from command line

setlocal enabledelayedexpansion

set VERSION=1.0.0
set JAR_FILE=%~dp0build\libs\IntelliJ_Platform_Plugin-1.0.0.jar

if not exist "%JAR_FILE%" (
    echo Error: JAR file not found at %JAR_FILE%
    echo Please run: gradlew.bat build
    exit /b 1
)

REM Show help if no arguments or help requested
if "%1"=="" (
    java -cp "%JAR_FILE%" com.giofrac.TestGeneratorCLI --help
    exit /b 0
)

if "%1"=="--help" (
    java -cp "%JAR_FILE%" com.giofrac.TestGeneratorCLI --help
    exit /b 0
)

REM Pass all arguments to the Java class
java -cp "%JAR_FILE%" com.giofrac.TestGeneratorCLI %*

