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

        // Enhanced pattern to match public methods with better generic support
        Pattern methodPattern = Pattern.compile(
            "public\\s+(?:static\\s+)?([\\w<>\\[\\],\\s.?]+?)\\s+([\\w]+)\\s*\\(([^)]*)\\)\\s*(?:throws\\s+[^\\{]+)?",
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

            // Parse parameters with better handling
            String[] paramTypes = new String[0];
            String[] paramNames = new String[0];

            if (!parameters.isEmpty()) {
                paramTypes = parseParameterTypes(parameters);
                paramNames = parseParameterNames(parameters);
            }

            methods.add(new TestGeneratorCLI.MethodInfo(methodName, returnType, paramTypes, paramNames));
        }

        return methods;
    }

    private String[] parseParameterTypes(String parameters) {
        List<String> types = new ArrayList<>();
        int depth = 0;
        StringBuilder currentType = new StringBuilder();

        for (int i = 0; i < parameters.length(); i++) {
            char c = parameters.charAt(i);

            if (c == '<') {
                depth++;
                currentType.append(c);
            } else if (c == '>') {
                depth--;
                currentType.append(c);
            } else if (c == ',' && depth == 0) {
                // End of parameter
                String type = extractTypeFromParam(currentType.toString().trim());
                if (!type.isEmpty()) {
                    types.add(type);
                }
                currentType.setLength(0);
            } else {
                currentType.append(c);
            }
        }

        // Add the last parameter
        String type = extractTypeFromParam(currentType.toString().trim());
        if (!type.isEmpty()) {
            types.add(type);
        }

        return types.toArray(new String[0]);
    }

    private String[] parseParameterNames(String parameters) {
        List<String> names = new ArrayList<>();
        String[] params = parameters.split(",");

        for (String param : params) {
            param = param.trim();
            if (!param.isEmpty()) {
                String[] parts = param.split("\\s+");
                if (parts.length > 0) {
                    // Name is the last part, remove any trailing characters like commas
                    String name = parts[parts.length - 1].replaceAll("[^\\w]", "");
                    if (!name.isEmpty()) {
                        names.add(name);
                    }
                }
            }
        }

        return names.toArray(new String[0]);
    }

    private String extractTypeFromParam(String param) {
        if (param.isEmpty()) return "";

        // Remove parameter name (last word) and extra spaces
        String[] parts = param.split("\\s+");
        if (parts.length <= 1) return param.trim();

        // Rebuild type without the name
        StringBuilder type = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) type.append(" ");
            type.append(parts[i]);
        }

        return type.toString().trim();
    }

    private String getClassName() {
        Pattern classPattern = Pattern.compile("public\\s+class\\s+(\\w+)");
        Matcher matcher = classPattern.matcher(sourceCode);
        return matcher.find() ? matcher.group(1) : "";
    }
}
