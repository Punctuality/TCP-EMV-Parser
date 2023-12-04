package site.sergeyfedorov.util.numbers;



import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BigIntegerBuilderTest {

    @Test
    public void testConstructSimpleInteger() {
        BigIntegerBuilder builder = new BigIntegerBuilder();
        builder.addDigit(1);
        builder.addDigit(2);
        builder.addDigit(3);
        builder.addDigit(4);
        builder.addDigit(5);
        builder.addDigit(6);
        builder.addDigit(7);
        builder.addDigit(8);
        builder.addDigit(9);
        builder.addDigit(0);
        assertEquals(BigInteger.valueOf(1234567890), builder.build());
    }

    @Test
    public void testConstructBigInteger() {
        BigIntegerBuilder builder = new BigIntegerBuilder();
        for (int i = 1; i < 1000; i++) {
            builder.addDigit(i % 10);
        }

        assertEquals(999, builder.build().toString().length());
    }

    @Test
    public void testAddInvalidDigit() {
        BigIntegerBuilder builder = new BigIntegerBuilder();
        assertThrows(IllegalArgumentException.class, () -> {
            builder.addDigit(-1);
        });
    }

    @Test
    public void testAddInvalidDigit2() {
        BigIntegerBuilder builder = new BigIntegerBuilder();
        assertThrows(IllegalArgumentException.class, () -> {
            builder.addDigit(10);
        });
    }

    @Test
    public void testConstructNothing() {
        BigIntegerBuilder builder = new BigIntegerBuilder();
        assertThrows(NumberFormatException.class, () -> {
            assertEquals(BigInteger.ZERO, builder.build());
        });
    }
}
