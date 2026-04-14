# Claude Code Guidelines for Test Generator Project

## Project Overview
This is an IntelliJ Platform Plugin project that generates JUnit 5 test skeletons from Java methods. The tool eliminates repetitive test boilerplate code by automatically creating complete, runnable test classes.

## Core Architecture
- **CLI Tool**: `TestGeneratorCLI` - Command-line interface for batch test generation
- **Parser**: `JavaMethodParser` - Extracts method signatures using regex patterns
- **Generator**: `StandaloneTestGenerator` - Creates JUnit test code with intelligent defaults
- **Plugin**: Placeholder classes for IntelliJ IDE integration (`GenerateTestAction`, `TestFileGenerator`)

## Code Structure
```
src/main/java/com/giofrac/
├── Calculator.java              # Example implementation class
├── Main.java                    # Demo entry point
├── StandaloneTestGenerator.java # Core test generation logic
├── JavaMethodParser.java        # Method signature extraction
├── TestGeneratorCLI.java        # CLI interface
├── GenerateTestAction.java      # IntelliJ action (placeholder)
├── TestGeneratorDialog.java     # UI dialog (placeholder)
└── TestFileGenerator.java       # File creation (placeholder)
```

## Coding Standards
- **Language**: Java 17+ with modern features
- **Testing**: JUnit 5 with `assertNotNull` for basic assertions
- **Build**: Gradle 8.5+ with Kotlin DSL
- **Style**: Standard Java naming conventions, 4-space indentation
- **Documentation**: Javadoc for public methods, inline comments for complex logic

## Default Parameter Values
When generating test code, use these intelligent defaults:
- `int` → `1`
- `String` → `"test"`
- `boolean` → `true`
- `double` → `1.0`
- `float` → `1.0f`
- `long` → `1L`
- `char` → `'a'`
- `byte` → `(byte)1`
- `short` → `(short)1`
- `Object` → `null`

## Common Tasks
1. **Add new parameter type support**: Update `getDefaultValue()` in `StandaloneTestGenerator`
2. **Improve method parsing**: Enhance regex patterns in `JavaMethodParser`
3. **Add CLI options**: Modify argument parsing in `TestGeneratorCLI`
4. **Extend IntelliJ integration**: Implement full `AnAction` subclasses

## Testing
- Run `./gradlew test` to execute JUnit tests
- `CalculatorTest.java` demonstrates generated output
- Test both CLI and library usage modes

## Build Commands
- `./gradlew build` - Compile and test
- `./gradlew jar` - Create JAR artifact
- `./gradlew clean` - Clean build directory

## File Generation Pattern
Generated test files follow this structure:
```java
package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClassNameTest {
    @Test
    void testMethodName() {
        ClassName instance = new ClassName();
        ReturnType result = instance.methodName(defaultParams);
        assertNotNull(result);
    }
}
```

## IntelliJ Plugin Notes
- Plugin descriptor: `src/main/resources/META-INF/plugin.xml`
- Action registered in editor popup menu
- Requires IntelliJ Platform SDK for full development
- Current implementation uses placeholders for IDE features

## Development Workflow
1. Make changes to core classes
2. Run `./gradlew test` to validate
3. Test CLI with `./gradlew build && java -cp build/libs/*.jar com.giofrac.TestGeneratorCLI src/main/java/com/giofrac/Calculator.java`
4. Update documentation in README.md and DEV_TO_ARTICLE.md

## Key Classes to Understand
- `StandaloneTestGenerator`: Main generation logic
- `JavaMethodParser`: Regex-based source parsing
- `TestGeneratorCLI`: Command-line argument handling
- `Calculator`: Example class for testing

## Performance Considerations
- Use efficient regex patterns for method extraction
- Avoid unnecessary file I/O operations
- Keep generated code minimal and readable

## Future Enhancements
- Full IntelliJ plugin implementation
- Mockito integration for mocking
- Multiple test framework support (TestNG, Spock)
- Advanced assertion generation
- Code coverage integration
