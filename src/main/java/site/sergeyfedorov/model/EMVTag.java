package site.sergeyfedorov.model;

public record EMVTag(int rawTagId, EMVTagId tagId, short tagValueLen, byte[] tagValue) {}
