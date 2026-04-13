package com.giofrac;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts method information from Java source files.
 */
public class JavaMethodParser {

    private String sourceCode;

    public JavaMethodParser(String filePath) throws IOException {
        this.sourceCode = new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public List<TestGeneratorCLI.MethodInfo> extractMethods() {
        List<TestGeneratorCLI.MethodInfo> methods = new ArrayList<>();

        // Pattern to match public methods
        Pattern methodPattern = Pattern.compile(
            "public\\s+(?:static\\s+)?([\\w<>\\[\\],\\s]+?)\\s+([\\w]+)\\s*\\(([^)]*)\\)",
            Pattern.MULTILINE
        );

        Matcher matcher = methodPattern.matcher(sourceCode);
        Set<String> foundMethods = new HashSet<>();

        while (matcher.find()) {
            String returnType = matcher.group(1).trim();
            String methodName = matcher.group(2).trim();
            String parameters = matcher.group(3).trim();

            // Skip constructors and already processed methods
            if (methodName.equals(getClassName()) || foundMethods.contains(methodName)) {
                continue;
            }

            foundMethods.add(methodName);

            // Parse parameters
            String[] paramTypes = new String[0];
            String[] paramNames = new String[0];

            if (!parameters.isEmpty()) {
                String[] params = parameters.split(",");
                paramTypes = new String[params.length];
                paramNames = new String[params.length];

                for (int i = 0; i < params.length; i++) {
                    String param = params[i].trim();
                    String[] parts = param.split("\\s+");
                    if (parts.length >= 2) {
                        paramTypes[i] = parts[parts.length - 2]; // type is second to last
                        paramNames[i] = parts[parts.length - 1];  // name is last
                    }
                }
            }

            methods.add(new TestGeneratorCLI.MethodInfo(methodName, returnType, paramTypes, paramNames));
        }

        return methods;
    }

    private String getClassName() {
        Pattern classPattern = Pattern.compile("public\\s+class\\s+(\\w+)");
        Matcher matcher = classPattern.matcher(sourceCode);
        return matcher.find() ? matcher.group(1) : "";
    }
}

