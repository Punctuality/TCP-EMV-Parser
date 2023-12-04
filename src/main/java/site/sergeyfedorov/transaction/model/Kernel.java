package site.sergeyfedorov.transaction.model;

import lombok.Getter;
import site.sergeyfedorov.emv.model.tag.BinaryEMVTag;
import site.sergeyfedorov.emv.model.tag.EMVTag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public enum Kernel {
    VISA(0x03, "Visa"),
    MASTERCARD(0x02, "MasterCard"),
    AMEX(0x04, "American Express"),
    UNKNOWN();

    private final int kernelValue;
    private final String displayName;

    Kernel(int kernelValue, String displayName) {
        this.kernelValue = kernelValue;
        this.displayName = displayName;
    }

    Kernel() {
        this.kernelValue = -1;
        this.displayName = "Unknown";
    }

    private static final Map<Integer, Kernel> mappedKernels = new ConcurrentHashMap<>(values().length, 1.0F);
    static {
        for (Kernel kernel : values()) mappedKernels.put(kernel.kernelValue, kernel);
    }
    public static Kernel lookByValue(int value) {
        return mappedKernels.getOrDefault(value, UNKNOWN);
    }

    public static Kernel parseTag(EMVTag tag) {
        Kernel result = UNKNOWN;
        if (tag.tagId().getFormat() instanceof BinaryEMVTag tagFormat) {
            byte[] tagValue = tagFormat.parseTagValue(tag.tagValue());
            if (tagValue.length > 0) {
                result = lookByValue(tagValue[tagValue.length - 1]);

                // This is a fallback for tags that presumably contain an error in them,
                // but are presented in the example for this task
                //
                // Judging by EMV Spec:
                // Table 3-6: Default Value for Requested Kernel ID
                // Matching AID     - Value
                // American Express - 00000100b (which implies that provided example of 040000000000000 is wrong)
                // Discover         - 00000110b
                // JCB              - 00000101b
                // MasterCard       - 00000010b
                // UnionPay         - 00000111b
                // Visa             - 00000011b
                // Other            - 00000000b
                if (result == UNKNOWN && tagValue.length > 1) {
                    result = lookByValue(tagValue[0]);
                }
            }
        }
        return result;
    }
}
