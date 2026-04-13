package com.giofrac;

public class Calculator {

    public int somma(int a, int b) {
        return a + b;
    }

    public int sottrazione(int a, int b) {
        return a - b;
    }

    public int moltiplicazione(int a, int b) {
        return a * b;
    }

    public double divisione(double a, double b) {
        if (b == 0) throw new IllegalArgumentException("Cannot divide by zero");
        return a / b;
    }

    public String concatena(String a, String b) {
        return a + b;
    }
}

