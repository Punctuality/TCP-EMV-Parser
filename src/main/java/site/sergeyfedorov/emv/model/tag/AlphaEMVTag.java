package site.sergeyfedorov.emv.model.tag;

import site.sergeyfedorov.util.limits.LimitNumber;
import site.sergeyfedorov.util.limits.Limited;

import java.nio.charset.StandardCharsets;

public record AlphaEMVTag(LimitNumber min, LimitNumber max) implements EMVTagFormat<String> {
    private static final String ALPHA_REGEX = "[A-Za-z]*";

    @Override
    public boolean validateTagValue(byte[] tagValue) {
        String strRepr = new String(tagValue, StandardCharsets.US_ASCII);

        if (min instanceof Limited limitedMin && strRepr.length() < limitedMin.limit()) {
            return false;
        } else if (max instanceof Limited limitedMax && strRepr.length() > limitedMax.limit()) {
            return false;
        }

        return strRepr.matches(ALPHA_REGEX);
    }

    @Override
    public String parseTagValue(byte[] tagValue) {
        return new String(tagValue, StandardCharsets.US_ASCII);
    }
}
