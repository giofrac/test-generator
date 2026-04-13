# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [1.0.0] - 2026-04-13

### Added
- Initial MVP release
- Core `StandaloneTestGenerator` class for JUnit 5 test skeleton generation
- `JavaMethodParser` for extracting method signatures from Java source
- `TestGeneratorCLI` command-line interface for batch test generation
- Support for all primitive Java types with intelligent default values
- Package-aware test class generation
- Integration with JUnit 5 testing framework
- `testgen.sh` wrapper script for Unix/Linux/Mac
- `testgen.bat` wrapper script for Windows
- Comprehensive README with usage examples
- Example `Calculator` class with multiple test methods

### Planned for 2.0.0
- Full IntelliJ IDEA plugin with IDE integration
- Right-click context menu action
- Test generation dialog with configuration options
- Support for Mockito mocking framework
- Data-driven test generation
- Test coverage analysis integration

### Planned for 3.0.0
- Support for multiple test frameworks (TestNG, Spock)
- Advanced assertion templates
- Integration test skeleton generation
- Cloud sync for test templates
- IDE plugin marketplace distribution

## [Unreleased]

### Development
- Plugin action framework preparation
- Dialog UI scaffolding
- PSI-based file generation infrastructure

