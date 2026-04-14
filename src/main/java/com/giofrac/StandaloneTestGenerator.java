package com.giofrac;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Standalone test generator that works independently of IntelliJ Platform SDK.
 * This can be extended to work with IDE plugins.
 */
public class StandaloneTestGenerator {

    private String packageName;
    private String className;
    private String methodSignature;

    public StandaloneTestGenerator(String javaFilePath) throws IOException {
        parseJavaFile(javaFilePath);
    }

    private void parseJavaFile(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));

        // Extract package name
        Pattern packagePattern = Pattern.compile("package\\s+([\\w.]+);");
        Matcher packageMatcher = packagePattern.matcher(content);
        this.packageName = packageMatcher.find() ? packageMatcher.group(1) : "";

        // Extract class name
        Pattern classPattern = Pattern.compile("public\\s+class\\s+(\\w+)");
        Matcher classMatcher = classPattern.matcher(content);
        this.className = classMatcher.find() ? classMatcher.group(1) : "Unknown";
    }

    public String generateTestForMethod(String methodName, String methodReturnType,
                                        String[] paramTypes, String[] paramNames) {
        StringBuilder sb = new StringBuilder();

        // Package
        if (!packageName.isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }

        // Imports
        sb.append("import org.junit.jupiter.api.Test;\n");
        sb.append("import static org.junit.jupiter.api.Assertions.*;\n\n");

        // Class
        String testClassName = className + "Test";
        sb.append("public class ").append(testClassName).append(" {\n\n");

        // Test method
        String testMethodName = "test" + capitalizeMethodName(methodName);
        sb.append("    @Test\n");
        sb.append("    void ").append(testMethodName).append("() {\n");

        // Create instance and call method
        sb.append("        ").append(className).append(" instance = new ").append(className).append("();\n");

        // Call method with parameters
        String resultVar = generateMethodCall(sb, methodName, methodReturnType, paramTypes);

        // Generate intelligent assertions
        generateAssertions(sb, methodReturnType, resultVar);

        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    public String generateTestForMethodWithException(String methodName, String methodReturnType,
                                                   String[] paramTypes, String[] paramNames,
                                                   String exceptionType) {
        StringBuilder sb = new StringBuilder();

        // Package
        if (!packageName.isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }

        // Imports
        sb.append("import org.junit.jupiter.api.Test;\n");
        sb.append("import static org.junit.jupiter.api.Assertions.*;\n\n");

        // Class
        String testClassName = className + "Test";
        sb.append("public class ").append(testClassName).append(" {\n\n");

        // Test method for exception
        String testMethodName = "test" + capitalizeMethodName(methodName) + "ThrowsException";
        sb.append("    @Test\n");
        sb.append("    void ").append(testMethodName).append("() {\n");

        // Create instance
        sb.append("        ").append(className).append(" instance = new ").append(className).append("();\n");

        // Assert throws
        sb.append("        assertThrows(").append(exceptionType).append(".class, () -> {\n");
        sb.append("            ");

        if (!"void".equals(methodReturnType)) {
            sb.append("instance.").append(methodName).append("(");
        } else {
            sb.append("instance.").append(methodName).append("(");
        }

        // Add parameters
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(getDefaultValue(paramTypes[i]));
        }
        sb.append(");\n");
        sb.append("        });\n");

        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    private String capitalizeMethodName(String methodName) {
        if (methodName == null || methodName.isEmpty()) {
            return "Method";
        }
        return Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
    }

    private String generateMethodCall(StringBuilder sb, String methodName, String methodReturnType,
                                     String[] paramTypes) {
        String resultVar = null;

        if (!"void".equals(methodReturnType)) {
            resultVar = "result";
            sb.append("        ").append(methodReturnType).append(" ").append(resultVar)
              .append(" = instance.").append(methodName).append("(");
        } else {
            sb.append("        instance.").append(methodName).append("(");
        }

        // Add parameters
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(getDefaultValue(paramTypes[i]));
        }
        sb.append(");\n");

        return resultVar;
    }

    private void generateAssertions(StringBuilder sb, String methodReturnType, String resultVar) {
        if ("void".equals(methodReturnType)) {
            // For void methods, just ensure no exception was thrown
            sb.append("        // Method executed without throwing exceptions\n");
            return;
        }

        // Basic null check
        sb.append("        assertNotNull(").append(resultVar).append(");\n");

        // Type-specific assertions
        String trimmedType = methodReturnType.trim();
        switch (trimmedType) {
            case "int", "long", "short", "byte" -> {
                sb.append("        assertTrue(").append(resultVar).append(" >= 0, \"Result should be non-negative\");\n");
            }
            case "double", "float" -> {
                sb.append("        assertFalse(Double.isNaN(").append(resultVar).append("), \"Result should not be NaN\");\n");
                sb.append("        assertFalse(Double.isInfinite(").append(resultVar).append("), \"Result should not be infinite\");\n");
            }
            case "boolean" -> {
                // Boolean already has basic assertion
            }
            case "String" -> {
                sb.append("        assertFalse(").append(resultVar).append(".isEmpty(), \"Result should not be empty\");\n");
            }
            default -> {
                if (trimmedType.contains("[]")) {
                    // Array types
                    sb.append("        assertTrue(").append(resultVar).append(".length >= 0, \"Array should have non-negative length\");\n");
                }
            }
        }
    }

    public String getDefaultValue(String type) {
        String trimmedType = type.trim();

        // Handle array types
        if (trimmedType.endsWith("[]")) {
            String baseType = trimmedType.substring(0, trimmedType.length() - 2);
            return "new " + baseType + "[]{ " + getDefaultValue(baseType) + " }";
        }

        // Handle generic types (simplified)
        if (trimmedType.contains("<") && trimmedType.contains(">")) {
            // For collections like List<String>, create empty collections
            if (trimmedType.startsWith("List<") || trimmedType.startsWith("ArrayList<")) {
                return "new java.util.ArrayList<>()";
            }
            if (trimmedType.startsWith("Set<") || trimmedType.startsWith("HashSet<")) {
                return "new java.util.HashSet<>()";
            }
            if (trimmedType.startsWith("Map<") || trimmedType.startsWith("HashMap<")) {
                return "new java.util.HashMap<>()";
            }
            // For other generics, return null
            return "null";
        }

        return switch (trimmedType) {
            case "int" -> "1";
            case "String" -> "\"test\"";
            case "boolean" -> "true";
            case "double" -> "1.0";
            case "float" -> "1.0f";
            case "long" -> "1L";
            case "byte" -> "(byte)1";
            case "char" -> "'a'";
            case "short" -> "(short)1";
            case "Integer" -> "1";
            case "Boolean" -> "true";
            case "Double" -> "1.0";
            case "Float" -> "1.0f";
            case "Long" -> "1L";
            case "Byte" -> "(byte)1";
            case "Character", "Char" -> "'a'";
            case "Short" -> "(short)1";
            case "BigDecimal" -> "new java.math.BigDecimal(\"1.0\")";
            case "BigInteger" -> "new java.math.BigInteger(\"1\")";
            case "LocalDate" -> "java.time.LocalDate.now()";
            case "LocalDateTime" -> "java.time.LocalDateTime.now()";
            case "Instant" -> "java.time.Instant.now()";
            case "UUID" -> "java.util.UUID.randomUUID()";
            default -> "null";
        };
    }

    public void writeTestFile(String testCode, String outputDir) throws IOException {
        // Extract test class name from code
        Pattern classPattern = Pattern.compile("public\\s+class\\s+(\\w+)");
        Matcher matcher = classPattern.matcher(testCode);
        if (!matcher.find()) {
            throw new IOException("Could not extract test class name");
        }

        String testClassName = matcher.group(1);

        // Create directory if it doesn't exist
        File dir = new File(outputDir);
        dir.mkdirs();

        String filePath = outputDir + File.separator + testClassName + ".java";

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(testCode);
        }

        System.out.println("Test file generated: " + filePath);
    }

    public String getTestClassName() {
        return className + "Test";
    }

    public String getClassName() {
        return className;
    }
}
