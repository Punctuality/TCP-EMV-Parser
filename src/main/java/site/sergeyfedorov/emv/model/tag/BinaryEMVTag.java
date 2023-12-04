package site.sergeyfedorov.emv.model.tag;

import site.sergeyfedorov.util.limits.LimitNumber;
import site.sergeyfedorov.util.limits.Limited;

public record BinaryEMVTag(LimitNumber min, LimitNumber max) implements EMVTagFormat<byte[]> {
    @Override
    public boolean validateTagValue(byte[] tagValue) {
        return !(min instanceof Limited limitedMin && tagValue.length < limitedMin.limit()) &&
            !(max instanceof Limited limitedMax && tagValue.length > limitedMax.limit());
    }

    @Override
    public byte[] parseTagValue(byte[] tagValue) {
        return tagValue;
    }
}
