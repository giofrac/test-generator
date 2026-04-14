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

    public String generateTestWithMockito(String methodName, String methodReturnType,
                                        String[] paramTypes, String[] paramNames,
                                        String[] mockableParams) {
        StringBuilder sb = new StringBuilder();

        // Package
        if (!packageName.isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }

        // Imports
        sb.append("import org.junit.jupiter.api.Test;\n");
        sb.append("import org.junit.jupiter.api.extension.ExtendWith;\n");
        sb.append("import org.mockito.Mock;\n");
        sb.append("import org.mockito.junit.jupiter.MockitoExtension;\n");
        sb.append("import static org.junit.jupiter.api.Assertions.*;\n");
        sb.append("import static org.mockito.Mockito.*;\n\n");

        // Class with Mockito extension
        String testClassName = className + "Test";
        sb.append("@ExtendWith(MockitoExtension.class)\n");
        sb.append("public class ").append(testClassName).append(" {\n\n");

        // Add mock fields for mockable parameters
        for (String mockParam : mockableParams) {
            if (mockParam != null && !mockParam.trim().isEmpty()) {
                sb.append("    @Mock\n");
                sb.append("    private ").append(mockParam.trim()).append(" mock").append(mockParam.trim()).append(";\n\n");
            }
        }

        // Test method
        String testMethodName = "test" + capitalizeMethodName(methodName);
        sb.append("    @Test\n");
        sb.append("    void ").append(testMethodName).append("() {\n");

        // Create instance with mocked dependencies
        sb.append("        ").append(className).append(" instance = new ").append(className).append("(");
        for (int i = 0; i < mockableParams.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append("mock").append(mockableParams[i].trim());
        }
        sb.append(");\n");

        // Setup mocks with when-thenReturn
        for (int i = 0; i < paramTypes.length; i++) {
            if (isMockableType(paramTypes[i])) {
                sb.append("        when(mock").append(paramTypes[i]).append(".").append(getMockMethod(paramTypes[i]))
                  .append("()).thenReturn(").append(getDefaultValue(paramTypes[i])).append(");\n");
            }
        }

        // Call method with parameters
        String resultVar = generateMethodCall(sb, methodName, methodReturnType, paramTypes);

        // Generate intelligent assertions
        generateAssertions(sb, methodReturnType, resultVar);

        // Verify mock interactions
        for (String paramType : paramTypes) {
            if (isMockableType(paramType)) {
                sb.append("        verify(mock").append(paramType).append(")").append(getVerifyMethod(paramType)).append(";\n");
            }
        }

        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    public String generateParameterizedTest(String methodName, String methodReturnType,
                                          String[] paramTypes, String[] paramNames) {
        StringBuilder sb = new StringBuilder();

        // Package
        if (!packageName.isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }

        // Imports
        sb.append("import org.junit.jupiter.api.Test;\n");
        sb.append("import org.junit.jupiter.params.ParameterizedTest;\n");
        sb.append("import org.junit.jupiter.params.provider.ValueSource;\n");
        sb.append("import org.junit.jupiter.params.provider.CsvSource;\n");
        sb.append("import static org.junit.jupiter.api.Assertions.*;\n\n");

        // Class
        String testClassName = className + "Test";
        sb.append("public class ").append(testClassName).append(" {\n\n");

        // Parameterized test for single parameter methods
        if (paramTypes.length == 1) {
            String paramType = paramTypes[0];
            String paramName = paramNames[0];

            sb.append("    @ParameterizedTest\n");
            sb.append("    @ValueSource(").append(getValueSourceForType(paramType)).append(")\n");
            sb.append("    void test").append(capitalizeMethodName(methodName)).append("(").append(paramType).append(" ").append(paramName).append(") {\n");

            // Create instance
            sb.append("        ").append(className).append(" instance = new ").append(className).append("();\n");

            // Call method
            if (!"void".equals(methodReturnType)) {
                sb.append("        ").append(methodReturnType).append(" result = instance.").append(methodName).append("(").append(paramName).append(");\n");
                generateAssertions(sb, methodReturnType, "result");
            } else {
                sb.append("        instance.").append(methodName).append("(").append(paramName).append(");\n");
                sb.append("        // Method executed without throwing exceptions\n");
            }

            sb.append("    }\n\n");
        }

        // CSV parameterized test for multiple parameters
        if (paramTypes.length > 1) {
            sb.append("    @ParameterizedTest\n");
            sb.append("    @CsvSource({\n");
            sb.append("        \"").append(getCsvValuesForTypes(paramTypes)).append("\"\n");
            sb.append("    })\n");
            sb.append("    void test").append(capitalizeMethodName(methodName)).append("(");

            // Method parameters
            for (int i = 0; i < paramTypes.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(paramTypes[i]).append(" ").append(paramNames[i]);
            }
            sb.append(") {\n");

            // Create instance
            sb.append("        ").append(className).append(" instance = new ").append(className).append("();\n");

            // Call method
            if (!"void".equals(methodReturnType)) {
                sb.append("        ").append(methodReturnType).append(" result = instance.").append(methodName).append("(");
                for (int i = 0; i < paramNames.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(paramNames[i]);
                }
                sb.append(");\n");
                generateAssertions(sb, methodReturnType, "result");
            } else {
                sb.append("        instance.").append(methodName).append("(");
                for (int i = 0; i < paramNames.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(paramNames[i]);
                }
                sb.append(");\n");
                sb.append("        // Method executed without throwing exceptions\n");
            }

            sb.append("    }\n\n");
        }

        // Regular test as fallback
        sb.append("    @Test\n");
        sb.append("    void test").append(capitalizeMethodName(methodName)).append("Default() {\n");

        // Create instance
        sb.append("        ").append(className).append(" instance = new ").append(className).append("();\n");

        // Call method with default parameters
        String resultVar = generateMethodCall(sb, methodName, methodReturnType, paramTypes);
        generateAssertions(sb, methodReturnType, resultVar);

        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    public String generateTestNGTest(String methodName, String methodReturnType,
                                   String[] paramTypes, String[] paramNames) {
        StringBuilder sb = new StringBuilder();

        // Package
        if (!packageName.isEmpty()) {
            sb.append("package ").append(packageName).append(";\n\n");
        }

        // Imports
        sb.append("import org.testng.annotations.Test;\n");
        sb.append("import org.testng.Assert;\n\n");

        // Class
        String testClassName = className + "Test";
        sb.append("public class ").append(testClassName).append(" {\n\n");

        // Test method
        String testMethodName = "test" + capitalizeMethodName(methodName);
        sb.append("    @Test\n");
        sb.append("    public void ").append(testMethodName).append("() {\n");

        // Create instance and call method
        sb.append("        ").append(className).append(" instance = new ").append(className).append("();\n");

        // Call method with parameters
        String resultVar = generateTestNGMethodCall(sb, methodName, methodReturnType, paramTypes);

        // Generate TestNG assertions
        generateTestNGAssertions(sb, methodReturnType, resultVar);

        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    private String generateTestNGMethodCall(StringBuilder sb, String methodName, String methodReturnType,
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

    private void generateTestNGAssertions(StringBuilder sb, String methodReturnType, String resultVar) {
        if ("void".equals(methodReturnType)) {
            // For void methods, just ensure no exception was thrown
            sb.append("        // Method executed without throwing exceptions\n");
            return;
        }

        // Basic null check
        sb.append("        Assert.assertNotNull(").append(resultVar).append(");\n");

        // Type-specific assertions
        String trimmedType = methodReturnType.trim();
        switch (trimmedType) {
            case "int", "long", "short", "byte" -> {
                sb.append("        Assert.assertTrue(").append(resultVar).append(" >= 0, \"Result should be non-negative\");\n");
            }
            case "double", "float" -> {
                sb.append("        Assert.assertFalse(Double.isNaN(").append(resultVar).append("), \"Result should not be NaN\");\n");
                sb.append("        Assert.assertFalse(Double.isInfinite(").append(resultVar).append("), \"Result should not be infinite\");\n");
            }
            case "boolean" -> {
                // Boolean already has basic assertion
            }
            case "String" -> {
                sb.append("        Assert.assertFalse(").append(resultVar).append(".isEmpty(), \"Result should not be empty\");\n");
            }
            default -> {
                if (trimmedType.contains("[]")) {
                    // Array types
                    sb.append("        Assert.assertTrue(").append(resultVar).append(".length >= 0, \"Array should have non-negative length\");\n");
                }
            }
        }
    }

    private boolean isMockableType(String type) {
        // Consider interfaces and abstract classes as mockable
        String trimmed = type.trim();
        return trimmed.contains("Service") || trimmed.contains("Repository") ||
               trimmed.contains("Dao") || trimmed.contains("Client") ||
               trimmed.endsWith("Interface") || trimmed.startsWith("I");
    }

    private String getMockMethod(String type) {
        // Simple heuristic for mock method names
        if (type.contains("Service")) return "process";
        if (type.contains("Repository")) return "findAll";
        if (type.contains("Dao")) return "getData";
        if (type.contains("Client")) return "call";
        return "execute";
    }

    private String getVerifyMethod(String type) {
        // Simple verification methods
        if (type.contains("Service")) return ".process(any())";
        if (type.contains("Repository")) return ".findAll()";
        if (type.contains("Dao")) return ".getData()";
        if (type.contains("Client")) return ".call(any())";
        return ".execute()";
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

    private String getValueSourceForType(String type) {
        return switch (type.trim()) {
            case "int", "Integer" -> "ints = {1, 2, 0, -1, 100}";
            case "String" -> "strings = {\"test\", \"\", \"hello world\", \"123\"}";
            case "boolean", "Boolean" -> "booleans = {true, false}";
            case "double", "Double" -> "doubles = {1.0, 0.0, -1.0, Double.MAX_VALUE}";
            case "float", "Float" -> "floats = {1.0f, 0.0f, -1.0f}";
            case "long", "Long" -> "longs = {1L, 0L, -1L, Long.MAX_VALUE}";
            default -> "strings = {\"test\"}";
        };
    }

    private String getCsvValuesForTypes(String[] types) {
        StringBuilder csv = new StringBuilder();
        for (int i = 0; i < Math.min(types.length, 3); i++) { // Limit to 3 test cases
            if (i > 0) csv.append("\", \"");
            for (int j = 0; j < types.length; j++) {
                if (j > 0) csv.append(", ");
                csv.append(getCsvValueForType(types[j]));
            }
        }
        return csv.toString();
    }

    private String getCsvValueForType(String type) {
        return switch (type.trim()) {
            case "int", "Integer" -> "1";
            case "String" -> "test";
            case "boolean", "Boolean" -> "true";
            case "double", "Double" -> "1.0";
            case "float", "Float" -> "1.0f";
            case "long", "Long" -> "1L";
            default -> "test";
        };
    }
}
