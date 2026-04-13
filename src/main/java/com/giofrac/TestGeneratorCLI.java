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
                // Generate for all non-private methods
                for (MethodInfo method : methods) {
                    generateTest(generator, method, outputDir);
                }
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

