package site.sergeyfedorov.emv.model;

import java.util.Arrays;

public record EMVTransmission(int dataLen, EMVMessage[] messages) {
    @Override
    public String toString() {
        return "EMVTransmission{" +
            "dataLen=" + dataLen +
            ", messages=" + Arrays.toString(messages) +
            '}';
    }
}
