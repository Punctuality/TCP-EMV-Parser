package site.sergeyfedorov.emv.model.tag;

import site.sergeyfedorov.util.limits.LimitNumber;
import site.sergeyfedorov.util.limits.Limited;
import site.sergeyfedorov.util.numbers.BigIntegerBuilder;
import site.sergeyfedorov.util.numbers.DigitChecker;

import java.math.BigInteger;

public record CompressedNumericEMVTag(LimitNumber min, LimitNumber max) implements EMVTagFormat<BigInteger> {
    @Override
    public boolean validateTagValue(byte[] tagValue) {
        int[] digits = new int[tagValue.length * 2];
        for (int i = 0; i < tagValue.length; i++) {
            int firstDigit = (tagValue[i] & 0xF0) >> 4;
            int secondDigit = tagValue[i] & 0x0F;

            digits[i * 2] = firstDigit;
            digits[i * 2 + 1] = secondDigit;
        }

        int startedTrailingIdx = -1;
        for (int i = 0; i < digits.length; i++) {
            if (!DigitChecker.isDigit(digits[i]) && startedTrailingIdx == -1) {
                if (digits[i] == 0xF) {
                    startedTrailingIdx = i;
                } else {
                    return false;
                }
            } else if (startedTrailingIdx != -1 && digits[i] != 0xF) {
                return false;
            }
        }

        if (min instanceof Limited limitedMin && startedTrailingIdx < limitedMin.limit()) {
            return false;
        } else if (max instanceof Limited limitedMax && startedTrailingIdx > limitedMax.limit()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public BigInteger parseTagValue(byte[] tagValue) {
        BigIntegerBuilder builder = new BigIntegerBuilder();

        for (byte b : tagValue) {
            int firstDigit = (b & 0xF0) >> 4;
            int secondDigit = b & 0x0F;

            if (firstDigit == 0xF) {
                break;
            } else if (secondDigit == 0xF) {
                builder.addDigit(firstDigit);
                break;
            } else {
                builder.addDigit(firstDigit);
                builder.addDigit(secondDigit);
            }
        }

        return builder.build();
    }
}
