# Contributing to Test Generator

## Development Setup

### Prerequisites
- Java 17+
- Gradle 8.5+
- Git

### Clone & Build

```bash
git clone https://github.com/giofrac/testgen.git
cd testgen
./gradlew build
```

## Project Structure

```
src/main/java/com/giofrac/
├── StandaloneTestGenerator.java    # Core generation logic
├── JavaMethodParser.java           # Java source parsing
├── TestGeneratorCLI.java          # CLI interface
├── GenerateTestAction.java         # IDE action (WIP)
├── TestGeneratorDialog.java        # IDE dialog (WIP)
└── TestFileGenerator.java          # IDE file creation (WIP)
```

## Key Classes

### StandaloneTestGenerator
Generates JUnit 5 test code from method information. Uses regex to parse Java files and generates idiomatic test class structures.

**Key Methods:**
- `generateTestForMethod()` - Main generation logic
- `getDefaultValue()` - Type-aware default value mapping
- `writeTestFile()` - Persists generated code to disk

### JavaMethodParser
Parses Java source files to extract method information.

**Key Methods:**
- `extractMethods()` - Returns list of all public methods
- `getClassName()` - Extracts class name from source

### TestGeneratorCLI
Command-line interface for the generator.

**Supports:**
- `--method <name>` - Generate for specific method
- `--output <dir>` - Custom output directory
- `--help` / `--version` - Usage information

## Testing

Run the test suite:

```bash
./gradlew test
```

## Code Style

- Follow standard Java conventions
- Use 4-space indentation
- Provide JavaDoc for public APIs
- Keep methods focused and testable

## Building for Release

### Create Distribution

```bash
./gradlew build
```

JAR artifact: `build/libs/IntelliJ_Platform_Plugin-1.0.0.jar`

### Version Increment

1. Update version in `build.gradle.kts`
2. Update `TestGeneratorCLI.VERSION`
3. Document changes in `CHANGELOG.md`
4. Commit and tag: `git tag v1.1.0`

## Feature Development

### Adding New Test Framework Support

1. Extend `StandaloneTestGenerator`
2. Add framework-specific import handling
3. Add test annotation mapping
4. Update `CHANGELOG.md`

### Adding IDE Plugin Features

1. Implement in `GenerateTestAction`
2. Create corresponding UI in `TestGeneratorDialog`
3. Use `TestFileGenerator` for PSI-based file creation
4. Update `plugin.xml` descriptor

## Git Workflow

1. Create feature branch: `git checkout -b feature/my-feature`
2. Commit changes: `git commit -m "Add feature description"`
3. Push to remote: `git push origin feature/my-feature`
4. Submit pull request

## Reporting Issues

When reporting bugs, include:
- Java source file that causes the issue
- Expected vs actual output
- Java/Gradle version information
- Error stack trace (if applicable)

## Documentation

- Update `README.md` for user-facing changes
- Add JavaDoc for API changes
- Update examples if behavior changes

## License

By contributing, you agree your code will be licensed under the MIT License.

