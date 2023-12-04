package site.sergeyfedorov.emv.model.tag;

public record DOLTag() implements EMVTagFormat<byte[]> {
    @Override
    public boolean validateTagValue(byte[] tagValue) {
        return true;
    }

    @Override
    public byte[] parseTagValue(byte[] tagValue) {
        return tagValue;
    }
}
