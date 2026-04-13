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
        String testMethodName = "test" + Character.toUpperCase(methodName.charAt(0)) +
                               methodName.substring(1);
        sb.append("    @Test\n");
        sb.append("    void ").append(testMethodName).append("() {\n");

        // Create instance and call method
        sb.append("        ").append(className).append(" instance = new ").append(className).append("();\n");

        // Call method with parameters
        if (!methodReturnType.equals("void")) {
            sb.append("        ").append(methodReturnType).append(" result = instance.").append(methodName).append("(");
        } else {
            sb.append("        instance.").append(methodName).append("(");
        }

        // Add parameters
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(getDefaultValue(paramTypes[i]));
        }
        sb.append(");\n");

        // Assertion
        if (!methodReturnType.equals("void")) {
            sb.append("        assertNotNull(result);\n");
        }

        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

    private String getDefaultValue(String type) {
        return switch (type.trim()) {
            case "int" -> "1";
            case "String" -> "\"test\"";
            case "boolean" -> "true";
            case "double" -> "1.0";
            case "float" -> "1.0f";
            case "long" -> "1L";
            case "byte" -> "(byte)1";
            case "char" -> "'a'";
            case "short" -> "(short)1";
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
}

