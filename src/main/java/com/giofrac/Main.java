package com.giofrac;

public class Main {
    public static void main(String[] args) throws Exception {
        String calculatorPath = "src/main/java/com/giofrac/Calculator.java";

        StandaloneTestGenerator generator = new StandaloneTestGenerator(calculatorPath);

        // Generate test for somma method
        String testCode = generator.generateTestForMethod(
            "somma",
            "int",
            new String[]{"int", "int"},
            new String[]{"a", "b"}
        );

        System.out.println("Generated Test Code:");
        System.out.println("====================");
        System.out.println(testCode);
        System.out.println("====================");

        // Write test file
        generator.writeTestFile(testCode, "src/test/java/com/giofrac");
    }
}