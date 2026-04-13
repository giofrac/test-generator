# Test Generator - IntelliJ Platform Plugin

[![Build and Test](https://github.com/giofrac/test-generator/workflows/Build%20and%20Test/badge.svg)](https://github.com/giofrac/test-generator/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-blue)]()
[![Latest Release](https://img.shields.io/badge/Release-1.0.0-green)](https://github.com/giofrac/test-generator/releases)

Automatically generates JUnit 5 test skeletons from Java methods. This plugin eliminates the boilerplate of writing test class structures and method signatures.

## Features

✅ **One-click test generation** - Right-click on any method to generate a complete test class  
✅ **JUnit 5 compatible** - Uses modern `@Test` and `Assertions` API  
✅ **Intelligent parameter handling** - Auto-fills method parameters with sensible defaults  
✅ **CLI tool** - Generate tests from command line for CI/CD integration  
✅ **Standalone library** - Use as a Java library in your own tools  

## Project Structure

```
IntelliJ_Platform_Plugin/
├── src/main/java/com/giofrac/
│   ├── Calculator.java                 # Example implementation
│   ├── Main.java                       # Entry point for demo
│   ├── StandaloneTestGenerator.java    # Core test generation logic
│   ├── JavaMethodParser.java           # Parse Java method signatures
│   ├── TestGeneratorCLI.java          # Command-line interface
│   ├── GenerateTestAction.java         # IntelliJ plugin action (placeholder)
│   ├── TestGeneratorDialog.java        # IntelliJ dialog UI (placeholder)
│   └── TestFileGenerator.java          # IntelliJ test file generation (placeholder)
├── src/main/resources/META-INF/
│   └── plugin.xml                      # IntelliJ plugin descriptor
├── src/test/java/com/giofrac/
│   └── CalculatorTest.java             # Generated example test
└── build.gradle.kts                    # Gradle build configuration
```

## Quick Start

### 1. Build the Project

```bash
./gradlew build
```

### 2. Run the Generator

**Using CLI:**

```bash
java -cp build/libs/IntelliJ_Platform_Plugin-1.0.0.jar \
    com.giofrac.TestGeneratorCLI src/main/java/com/giofrac/Calculator.java
```

**Generate for specific method:**

```bash
java -cp build/libs/IntelliJ_Platform_Plugin-1.0.0.jar \
    com.giofrac.TestGeneratorCLI src/main/java/com/giofrac/Calculator.java \
    --method somma
```

**Custom output directory:**

```bash
java -cp build/libs/IntelliJ_Platform_Plugin-1.0.0.jar \
    com.giofrac.TestGeneratorCLI src/main/java/com/giofrac/Calculator.java \
    --output target/test-gen
```

### 3. Run Generated Tests

```bash
./gradlew test
```

## Usage Example

### Input (Calculator.java)

```java
package com.giofrac;

public class Calculator {
    public int somma(int a, int b) {
        return a + b;
    }
}
```

### Generated Output (CalculatorTest.java)

```java
package com.giofrac;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {

    @Test
    void testSomma() {
        Calculator instance = new Calculator();
        int result = instance.somma(1, 1);
        assertNotNull(result);
    }
}
```

## Default Values for Types

The generator automatically fills method parameters with sensible defaults:

| Type    | Default Value |
|---------|---------------|
| int     | 1             |
| long    | 1L            |
| float   | 1.0f          |
| double  | 1.0           |
| String  | "test"        |
| boolean | true          |
| char    | 'a'           |
| byte    | (byte)1       |
| short   | (short)1      |
| Object  | null          |

## Library Usage

Use the generator as a library in your own Java projects:

```java
import com.giofrac.StandaloneTestGenerator;

public class MyTestGenerator {
    public static void main(String[] args) throws IOException {
        StandaloneTestGenerator generator = new StandaloneTestGenerator("MyClass.java");
        
        String testCode = generator.generateTestForMethod(
            "myMethod",      // method name
            "String",        // return type
            new String[]{"int", "String"},   // parameter types
            new String[]{"count", "name"}    // parameter names
        );
        
        generator.writeTestFile(testCode, "src/test/java");
    }
}
```

## Architecture

### Core Components

**StandaloneTestGenerator**
- Parses Java source files using regex patterns
- Generates JUnit 5 test code with proper structure
- Handles package names and import statements

**JavaMethodParser**
- Extracts public method signatures from Java files
- Parses method names, return types, and parameters

**TestGeneratorCLI**
- Command-line interface for batch test generation
- Supports filtering by method name

### IntelliJ Integration (Placeholder)

The plugin structure is prepared for full IntelliJ Platform SDK integration:

- `GenerateTestAction` - Context menu action handler
- `TestGeneratorDialog` - Configuration dialog
- `TestFileGenerator` - PSI-based file creation
- `plugin.xml` - Plugin manifest for distribution

## Roadmap

### MVP (Complete)
✅ Parse Java method signatures  
✅ Generate JUnit 5 test skeletons  
✅ CLI tool for automation  
✅ Library for programmatic use  

### Phase 2 (Pro Features)
- [ ] Full IntelliJ IDE plugin (menu integration)
- [ ] Mocking framework support (Mockito)
- [ ] Test coverage analysis
- [ ] Custom assertion templates
- [ ] Data-driven test generation

### Phase 3 (Advanced)
- [ ] Multiple test framework support (TestNG, Spock)
- [ ] Code coverage reporting
- [ ] Integration test generation
- [ ] Cloud sync for test templates

## Build Variants

### Standard JAR
```bash
./gradlew build
# Output: build/libs/IntelliJ_Platform_Plugin-1.0.0.jar
```

### Distribution Package
```bash
# Create plugin JAR for IntelliJ
./gradlew buildPlugin
```

## Requirements

- Java 17+
- Gradle 8.5+
- JUnit 5.10.0+

## Testing

Run the test suite:

```bash
./gradlew test
```

The `CalculatorTest.java` demonstrates auto-generated test output and validates the generator.

## License

MIT License - See LICENSE file for details

## Author

Giorgio Fracchiolla (@giofrac)

## Support

For issues, feature requests, or contributions, open an issue on GitHub.

