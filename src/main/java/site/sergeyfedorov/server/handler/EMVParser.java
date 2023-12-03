package site.sergeyfedorov.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.sergeyfedorov.exceptions.ServerPipelineException;
import site.sergeyfedorov.model.*;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

public class EMVParser extends ChannelInboundHandlerAdapter {

    private static final int START_OF_MESSAGE = 0x02;
    private static final int END_OF_MESSAGE = 0x03;
    private static final int TAG_FIRST_BYTE_MASK = 0x1F;
    private static final int CONTINUED_TAG_BYTE_MASK = 0x80;

    private static final Logger logger = LoggerFactory.getLogger(EMVParser.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (Objects.requireNonNull(msg) instanceof EMVRawTransfer(int msgLen, ByteBuf rawMessage)) {
            EMVTransmission decodedMessage = new EMVTransmission(msgLen, parseSeqOfMessages(rawMessage));
            ReferenceCountUtil.release(rawMessage);
            ctx.fireChannelRead(decodedMessage);
        } else {
            throw new ServerPipelineException("EMVParser received non-ByteBuf message: %s".formatted(msg));
        }
    }

    private EMVMessage[] parseSeqOfMessages(ByteBuf rawMessage) {
        LinkedList<EMVMessage> messages = new LinkedList<>();

        Optional<EMVMessage> nextMsg = readOneMessage(rawMessage);
        while (nextMsg.isPresent()) {
            messages.add(nextMsg.get());
            nextMsg = readOneMessage(rawMessage);
        }

        return messages.toArray(EMVMessage[]::new);
    }

    private Optional<EMVMessage> readOneMessage(ByteBuf rawMessage) {
        if (!rawMessage.isReadable()) {
            return Optional.empty();
        }

        int stxByte = rawMessage.readUnsignedByte();
        if (stxByte != START_OF_MESSAGE) {
            throw new ServerPipelineException("EMVParser received message with invalid STX byte: %x".formatted(stxByte));
        }
        int msgLen = rawMessage.readUnsignedByte();
        if (!rawMessage.isReadable(msgLen)) {
            throw new ServerPipelineException(
                "EMVParser doesn't have enough bytes to read message (have: %d, expected: %d)".formatted(
                    rawMessage.readableBytes(),
                    msgLen
                ));
        }

        LinkedList<EMVTag> tags = new LinkedList<>();
        while (!isEndOfMessage(rawMessage)) {
            tags.add(readOneTag(rawMessage));
        }

        EMVMessage decodedMessage = new EMVMessage(tags.toArray(EMVTag[]::new));
        return Optional.of(decodedMessage);
    }

    private boolean isEndOfMessage(ByteBuf rawMessage) {
        if (!rawMessage.isReadable()) {
            throw new ServerPipelineException("EMVParser didn't receive ETX byte");
        }
        rawMessage.markReaderIndex();
        boolean result = rawMessage.readUnsignedByte() == END_OF_MESSAGE;
        if (!result) {
            rawMessage.resetReaderIndex();
        }
        return result;
    }

    private EMVTag readOneTag(ByteBuf rawMessage) {
        int rawTagId = retrieveEMVTagId(rawMessage);
        EMVTagId tagId = EMVTagId.lookByTag(rawTagId);

        if (tagId == EMVTagId.UNKNOWN) {
            logger.warn("Unknown EMV tag: %x".formatted(rawTagId));
        }

        short tagValueLen = rawMessage.readUnsignedByte();
        byte[] tagValue = new byte[tagValueLen];

        rawMessage.readBytes(tagValue);

        return new EMVTag(rawTagId, tagId, tagValueLen, tagValue);
    }

    // TODO JavaDoc
    // Usually EMV tags are encoded in 1 or 2 bytes,
    // but sometimes they can be encoded in 3 bytes (We can support up to 4 bytes).
    private int retrieveEMVTagId(ByteBuf rawMessage) {
        int tagId = rawMessage.readUnsignedByte();

        if ((tagId & TAG_FIRST_BYTE_MASK) == TAG_FIRST_BYTE_MASK) {
            do {
                tagId = (tagId << 8) | rawMessage.readUnsignedByte();
            } while ((tagId & CONTINUED_TAG_BYTE_MASK) == CONTINUED_TAG_BYTE_MASK);
        }
        return tagId;
    }
}
