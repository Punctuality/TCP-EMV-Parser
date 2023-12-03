package site.sergeyfedorov.model;

public record EMVTransmission(int dataLen, EMVMessage[] messages) {}
