package com.giofrac;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CalculatorTest {

    @Test
    void testConcatena() {
        Calculator instance = new Calculator();
        String result = instance.concatena("test", "test");
        assertNotNull(result);
    }
}
