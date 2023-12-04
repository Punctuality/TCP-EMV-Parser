package site.sergeyfedorov.util.numbers;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DigitCheckerTest {

    @Test
    public void testValidDigits() {
        for (int i = 0; i < 10; i++) {
            assertTrue(DigitChecker.isDigit(i));
        }
    }

    @Test
    public void testInvalidDigits() {
        for (int i = -100; i < 0; i++) {
            assertFalse(DigitChecker.isDigit(i));
        }
        for (int i = 10; i < 100; i++) {
            assertFalse(DigitChecker.isDigit(i));
        }
    }
}
