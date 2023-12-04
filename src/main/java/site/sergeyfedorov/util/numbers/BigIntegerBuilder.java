package site.sergeyfedorov.util.numbers;

import java.math.BigInteger;

public class BigIntegerBuilder {
    private final StringBuilder numberBuilder;

    public BigIntegerBuilder() {
        numberBuilder = new StringBuilder();
    }

    public void addDigit(int digit) {
        if (!DigitChecker.isDigit(digit)) {
            throw new IllegalArgumentException("Digit must be between 0 and 9");
        }
        numberBuilder.append(digit);
    }

    public BigInteger build() {
        return new BigInteger(numberBuilder.toString());
    }

}
