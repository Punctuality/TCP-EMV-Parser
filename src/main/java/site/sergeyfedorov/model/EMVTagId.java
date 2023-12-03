package site.sergeyfedorov.model;

import lombok.Getter;
import site.sergeyfedorov.util.limits.Limited;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static site.sergeyfedorov.util.limits.Unlimited.NO_LIMIT;

@Getter
public enum EMVTagId {
    KERNEL_IDENTIFIER( 0x9F2A, new BinaryEMVTag(NO_LIMIT, NO_LIMIT)),
    PAN(0x5A, new CompressedNumericEMVTag(NO_LIMIT, Limited.to(19))),
    TRANSACTION_CURRENCY_CODE(0x5F2A, new NumericEMVTag(Limited.to(3), Limited.to(3))),
    AMOUNT_AUTHORISED(0x9F02, new NumericEMVTag(NO_LIMIT, Limited.to(12))),
    AMOUNT_OTHER(0x9F03, new NumericEMVTag(NO_LIMIT, Limited.to(12))),

    UNKNOWN();

    private final int tag;
    private final EMVTagFormat format;

    private static final Map<Integer, EMVTagId> mappedTags = new ConcurrentHashMap<>(values().length);
    static {
        for (EMVTagId tag : values()) mappedTags.put(tag.tag, tag);
    }

    public static EMVTagId lookByTag(int tag) {
        return mappedTags.getOrDefault(tag, UNKNOWN);
    }

    EMVTagId(int tag, EMVTagFormat binaryEMVTag) {
        this.tag = tag;
        this.format = binaryEMVTag;
    }
    EMVTagId() {
        this.tag = -1;
        this.format = new BinaryEMVTag(NO_LIMIT, NO_LIMIT);
    }
}
