package site.sergeyfedorov.emv.model;

import io.netty.buffer.ByteBuf;

public record EMVRawTransfer(int messageLength, ByteBuf rawMessage) {}