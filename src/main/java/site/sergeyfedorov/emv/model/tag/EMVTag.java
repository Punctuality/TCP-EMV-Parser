package site.sergeyfedorov.emv.model.tag;

public record EMVTag(int rawTagId, EMVTagId tagId, short tagValueLen, byte[] tagValue) {
    @Override
    public String toString() {
        return "EMVTag{" +
            "rawTagId=" + rawTagId +
            ", tagId=" + tagId +
            ", tagValueLen=" + tagValueLen +
            '}';
    }
}
