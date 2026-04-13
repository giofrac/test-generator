---
title: "I Built an Open-Source Tool That Generates JUnit Tests Automatically"
description: "How I created Test Generator in 5 days - a CLI tool that eliminates test boilerplate"
tags: java, testing, junit, opensource, gradle
canonical_url: https://github.com/giofrac/test-generator
---

# I Built an Open-Source Tool That Generates JUnit Tests Automatically

## The Problem

As a Java developer, I noticed something frustrating: **developers spend 20-30% of their time writing test boilerplate code.**

Test files are typically 60-70% boilerplate:
- Class declarations
- Import statements
- Method signatures
- Placeholder assertions

This is repetitive, error-prone, and adds zero business value.

## The Idea

What if I could generate a complete, runnable JUnit 5 test skeleton from a Java method in **seconds**?

```java
// Input
public int somma(int a, int b) {
    return a + b;
}

// One command
./testgen.sh Calculator.java --method somma

// Output: Complete test file!
@Test
void testSomma() {
    Calculator instance = new Calculator();
    int result = instance.somma(1, 1);
    assertNotNull(result);
}
```

## The Solution: Test Generator

I built **Test Generator** - an open-source tool that:

✅ Parses Java method signatures  
✅ Generates complete test classes  
✅ Handles all primitive types intelligently  
✅ Works as CLI or library  
✅ Integrates with CI/CD  

### Key Features

**1. Intelligent Parameter Defaults**

The generator automatically fills parameters with sensible defaults:

```
int     → 1
String  → "test"
boolean → true
double  → 1.0
float   → 1.0f
long    → 1L
```

**2. Multiple Usage Modes**

CLI:
```bash
./testgen.sh Calculator.java --method somma
```

Library:
```java
StandaloneTestGenerator gen = new StandaloneTestGenerator("MyClass.java");
String test = gen.generateTestForMethod("method", "int", params, names);
gen.writeTestFile(test, "src/test/java");
```

**3. Cross-Platform Support**
- Windows (.bat script)
- Linux/Mac (.sh script)
- GitHub Actions CI/CD included

## Building It: 5-Day Timeline

I decided to build this as a rapid MVP:

**Day 1:** Project setup + Gradle configuration  
**Day 2:** Method parsing + CLI interface  
**Day 3:** Test generation engine + examples  
**Day 4:** Documentation + refinement  
**Day 5:** GitHub setup + v1.0.0 release  

### Tech Stack
- Java 17+
- Gradle 8.5+
- JUnit 5
- GitHub Actions

This stack choice was intentional:
- **Java 17** - modern language features
- **Gradle** - dependency management & plugin ecosystem
- **JUnit 5** - modern testing framework
- **GitHub Actions** - free CI/CD

## Open Source Release

**Repository:** https://github.com/giofrac/test-generator  
**License:** MIT  
**Release:** v1.0.0 available now  

The tool is production-ready and includes:
- 431 lines of core Java code
- Complete documentation with examples
- GitHub Actions CI/CD workflows
- Issue templates for bug reports and features
- Contribution guidelines

## Real-World Impact

### The Numbers
- Typical developer spends 20-30% of time on test setup
- Test files are 60-70% boilerplate
- One method takes ~2-3 minutes to test manually
- Test Generator generates in **0.5 seconds**

### The Value Proposition
1. **Immediate Value** - Saves time on every test file  
2. **Monetizable** - Free tier + Pro features (Mockito, coverage)  
3. **Extensible** - Easy to add more frameworks (TestNG, Spock)  
4. **Community-Ready** - Fully documented, welcomes contributions  

## Why This Matters

Test code is essential but tedious. Most developers:
1. Copy-paste from existing tests
2. Manually set up boilerplate
3. Fill in placeholder assertions
4. Run tests to see what breaks

With Test Generator:
1. One command
2. Complete test skeleton
3. Ready to customize
4. Immediate productivity boost

## Roadmap

### Phase 1 (Complete) ✅
- [x] MVP with CLI tool
- [x] Java method parsing
- [x] JUnit 5 generation
- [x] Documentation

### Phase 2 (Planned)
- [ ] Full IntelliJ IDE plugin
- [ ] Mockito integration
- [ ] Test coverage analysis
- [ ] Custom assertion templates

### Phase 3 (Future)
- [ ] Multiple test framework support (TestNG, Spock)
- [ ] Code coverage reporting
- [ ] Integration test generation
- [ ] Cloud sync for test templates

## Getting Started

### Quick Installation

```bash
git clone https://github.com/giofrac/test-generator.git
cd test-generator
./gradlew build
```

### Quick Start

```bash
# Generate all tests
./testgen.sh Calculator.java

# Generate for specific method
./testgen.sh Calculator.java --method somma

# Custom output directory
./testgen.sh Calculator.java --output target/test-gen
```

### As a Library

```java
import com.giofrac.StandaloneTestGenerator;

public class MyApp {
    public static void main(String[] args) throws IOException {
        StandaloneTestGenerator gen = new StandaloneTestGenerator("MyClass.java");
        
        String test = gen.generateTestForMethod(
            "myMethod",      // method name
            "String",        // return type
            new String[]{"int", "String"},   // parameter types
            new String[]{"count", "name"}    // parameter names
        );
        
        gen.writeTestFile(test, "src/test/java");
    }
}
```

## The Community

I'm looking for:
- ✅ Java developers who want to save time
- ✅ Contributors interested in testing tools
- ✅ People who want to reduce boilerplate

**This is an open-source project built to solve a real problem. Feedback and PRs welcome!**

---

## Links

- 🚀 **[GitHub Repository](https://github.com/giofrac/test-generator)**
- 📖 **[Full Documentation](https://github.com/giofrac/test-generator#readme)**
- 🏷️ **[v1.0.0 Release](https://github.com/giofrac/test-generator/releases/tag/v1.0.0)**
- 📋 **[Open Issues](https://github.com/giofrac/test-generator/issues)**

---

Have you used a tool like this before? What features would you want to see?

Let me know in the comments! 👇

