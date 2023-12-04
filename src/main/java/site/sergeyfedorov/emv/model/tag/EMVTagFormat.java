package site.sergeyfedorov.emv.model.tag;

public sealed interface EMVTagFormat<T> permits
    BinaryEMVTag,
    NumericEMVTag,
    CompressedNumericEMVTag,
    AlphaNumericSpecialEMVTag,
    AlphaNumericEMVTag,
    AlphaEMVTag,
    DOLTag {

    boolean validateTagValue(byte[] tagValue);

    T parseTagValue(byte[] tagValue);
}

