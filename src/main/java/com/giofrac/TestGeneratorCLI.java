package com.giofrac;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CLI tool for generating JUnit tests from Java source files.
 * Usage: java com.giofrac.TestGeneratorCLI <sourcefile> [--method methodname] [--output dir]
 */
public class TestGeneratorCLI {

    private static final String VERSION = "1.0.0";

    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            System.exit(1);
        }

        try {
            if ("--version".equals(args[0])) {
                System.out.println("Test Generator CLI v" + VERSION);
                return;
            }

            if ("--help".equals(args[0])) {
                printHelp();
                return;
            }

            final String sourceFile = args[0];
            final String methodName = extractMethodName(args);
            final String outputDir = extractOutputDir(args);

            Path sourcePath = Paths.get(sourceFile);
            if (!Files.exists(sourcePath)) {
                System.err.println("Error: File not found: " + sourceFile);
                System.exit(1);
            }

            // Parse source file and extract methods
            JavaMethodParser parser = new JavaMethodParser(sourceFile);
            List<MethodInfo> methods = parser.extractMethods();

            if (methods.isEmpty()) {
                System.out.println("No public methods found in " + sourceFile);
                return;
            }

            StandaloneTestGenerator generator = new StandaloneTestGenerator(sourceFile);

            // Generate tests for specified method or all methods
            if (methodName != null) {
                MethodInfo method = methods.stream()
                    .filter(m -> m.name.equals(methodName))
                    .findFirst()
                    .orElse(null);

                if (method == null) {
                    System.err.println("Error: Method '" + methodName + "' not found");
                    System.exit(1);
                }

                generateTest(generator, method, outputDir);
            } else {
                // Generate a single test class with all methods
                generateAllTests(generator, methods, outputDir);
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void generateTest(StandaloneTestGenerator generator, MethodInfo method, String outputDir) throws IOException {
        String testCode = generator.generateTestForMethod(
            method.name,
            method.returnType,
            method.paramTypes,
            method.paramNames
        );

        generator.writeTestFile(testCode, outputDir);
        System.out.println("✓ Generated test for method: " + method.name);
    }

    private static void generateAllTests(StandaloneTestGenerator generator, List<MethodInfo> methods, String outputDir) throws IOException {
        StringBuilder testClassCode = new StringBuilder();

        // Package
        testClassCode.append("package generated;\n\n");

        // Imports
        testClassCode.append("import org.junit.jupiter.api.Test;\n");
        testClassCode.append("import static org.junit.jupiter.api.Assertions.*;\n\n");

        // Class declaration
        testClassCode.append("public class ").append(generator.getTestClassName()).append(" {\n\n");

        for (MethodInfo method : methods) {
            // Test method
            String testMethodName = "test" + capitalize(method.name);
            testClassCode.append("    @Test\n");
            testClassCode.append("    void ").append(testMethodName).append("() {\n");

            // Create instance
            testClassCode.append("        ").append(generator.getClassName()).append(" instance = new ")
                        .append(generator.getClassName()).append("();\n");

            // Generate method call and assertions
            generateTestMethodBody(testClassCode, method, generator);

            testClassCode.append("    }\n\n");
        }

        testClassCode.append("}\n");

        generator.writeTestFile(testClassCode.toString(), outputDir);
        System.out.println("✓ Generated comprehensive test class with " + methods.size() + " test methods");
    }

    private static void generateTestMethodBody(StringBuilder sb, MethodInfo method, StandaloneTestGenerator generator) {
        // Call method with parameters
        if (!"void".equals(method.returnType)) {
            sb.append("        ").append(method.returnType).append(" result = instance.").append(method.name).append("(");
        } else {
            sb.append("        instance.").append(method.name).append("(");
        }

        // Add parameters
        for (int i = 0; i < method.paramTypes.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(generator.getDefaultValue(method.paramTypes[i]));
        }
        sb.append(");\n");

        // Generate assertions
        if (!"void".equals(method.returnType)) {
            sb.append("        assertNotNull(result);\n");

            // Type-specific assertions
            String trimmedType = method.returnType.trim();
            switch (trimmedType) {
                case "int", "long", "short", "byte" -> {
                    sb.append("        assertTrue(result >= 0, \"Result should be non-negative\");\n");
                }
                case "double", "float" -> {
                    sb.append("        assertFalse(Double.isNaN(result), \"Result should not be NaN\");\n");
                    sb.append("        assertFalse(Double.isInfinite(result), \"Result should not be infinite\");\n");
                }
                case "String" -> {
                    sb.append("        assertFalse(result.isEmpty(), \"Result should not be empty\");\n");
                }
                default -> {
                    if (trimmedType.contains("[]")) {
                        sb.append("        assertTrue(result.length >= 0, \"Array should have non-negative length\");\n");
                    }
                }
            }
        } else {
            sb.append("        // Method executed without throwing exceptions\n");
        }
    }

    private static String extractMethodName(String[] args) {
        for (int i = 1; i < args.length; i++) {
            if ("--method".equals(args[i]) && i + 1 < args.length) {
                return args[++i];
            }
        }
        return null;
    }
    
    private static String extractOutputDir(String[] args) {
        for (int i = 1; i < args.length; i++) {
            if ("--output".equals(args[i]) && i + 1 < args.length) {
                return args[++i];
            }
        }
        return "src/test/java";
    }
    
    private static void printHelp() {
        System.out.println("Test Generator CLI v" + VERSION);
        System.out.println();
        System.out.println("Usage: java com.giofrac.TestGeneratorCLI <sourcefile> [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --method <name>      Generate test for specific method");
        System.out.println("  --output <dir>       Output directory (default: src/test/java)");
        System.out.println("  --help               Show this help message");
        System.out.println("  --version            Show version information");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java com.giofrac.TestGeneratorCLI Calculator.java");
        System.out.println("  java com.giofrac.TestGeneratorCLI Calculator.java --method somma");
        System.out.println("  java com.giofrac.TestGeneratorCLI Calculator.java --output target/test");
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    static class MethodInfo {
        String name;
        String returnType;
        String[] paramTypes;
        String[] paramNames;

        MethodInfo(String name, String returnType, String[] paramTypes, String[] paramNames) {
            this.name = name;
            this.returnType = returnType;
            this.paramTypes = paramTypes;
            this.paramNames = paramNames;
        }
    }
}
