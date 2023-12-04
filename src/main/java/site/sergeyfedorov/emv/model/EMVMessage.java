package site.sergeyfedorov.emv.model;

import site.sergeyfedorov.emv.model.tag.EMVTag;

import java.util.Arrays;

public record EMVMessage(EMVTag[] tags) {
    @Override
    public String toString() {
        return "EMVMessage{" +
            "tags=" + Arrays.toString(tags) +
            '}';
    }
}