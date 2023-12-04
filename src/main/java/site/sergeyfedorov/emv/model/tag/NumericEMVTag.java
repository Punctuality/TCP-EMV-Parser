package site.sergeyfedorov.emv.model.tag;

import site.sergeyfedorov.util.limits.LimitNumber;
import site.sergeyfedorov.util.limits.Limited;
import site.sergeyfedorov.util.numbers.BigIntegerBuilder;
import site.sergeyfedorov.util.numbers.DigitChecker;

import java.math.BigInteger;

public record NumericEMVTag(LimitNumber min, LimitNumber max) implements EMVTagFormat<BigInteger> {
    @Override
    public boolean validateTagValue(byte[] tagValue) {
        int[] digits = new int[tagValue.length * 2];
        for (int i = 0; i < tagValue.length; i++) {
            int firstDigit = (tagValue[i] & 0xF0) >> 4;
            int secondDigit = tagValue[i] & 0x0F;

            digits[i * 2] = firstDigit;
            digits[i * 2 + 1] = secondDigit;
        }

        int startedNonZeroIdx = -1;
        for (int i = 0; i < digits.length; i++) {
            if (!DigitChecker.isDigit(digits[i])) {
                return false;
            }
            if (digits[i] != 0 && startedNonZeroIdx == -1) {
                startedNonZeroIdx = i;
            }
        }

        if (min instanceof Limited limitedMin && (digits.length - startedNonZeroIdx) < limitedMin.limit()) {
            return false;
        } else if (max instanceof Limited limitedMax && (digits.length - startedNonZeroIdx) > limitedMax.limit()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public BigInteger parseTagValue(byte[] tagValue) {
        BigIntegerBuilder builder = new BigIntegerBuilder();

        boolean startAdding = false;
        for (byte b : tagValue) {
            int firstDigit = (b & 0xF0) >> 4;
            int secondDigit = b & 0x0F;

            if (startAdding) {
                builder.addDigit(firstDigit);
                builder.addDigit(secondDigit);
            } else if (firstDigit != 0) {
                startAdding = true;
                builder.addDigit(firstDigit);
                builder.addDigit(secondDigit);
            } else if (secondDigit != 0) {
                startAdding = true;
                builder.addDigit(secondDigit);
            }
        }

        return builder.build();
    }
}
