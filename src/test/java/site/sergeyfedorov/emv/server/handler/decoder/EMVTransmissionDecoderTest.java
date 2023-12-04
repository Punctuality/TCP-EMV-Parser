package site.sergeyfedorov.emv.server.handler.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;
import site.sergeyfedorov.emv.exceptions.ServerPipelineException;
import site.sergeyfedorov.emv.model.EMVMessage;
import site.sergeyfedorov.emv.model.EMVRawTransfer;
import site.sergeyfedorov.emv.model.EMVTransmission;
import site.sergeyfedorov.emv.model.tag.EMVTagId;
import site.sergeyfedorov.emv.server.handler.LoggingHandler;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class EMVTransmissionDecoderTest {
    @Test
    public void testSampleTransmission() throws InterruptedException {
        byte[] validPacket = new byte[]{
            0x00, 0x1B, 0x02, 0x18, (byte) 0x9F, 0x2A, 0x01, 0x02,
            (byte) 0x9F, 0x02, 0x02, 0x01, 0x00, 0x5A, 0x08, 0x41,
            0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x5F,
            0x2A, 0x02, 0x09, 0x78, 0x03
        };

        EmbeddedChannel channel = new EmbeddedChannel(
            new EMVBytesDecoder(),
            new EMVTransmissionDecoder()
        );

        ByteBuf buf = Unpooled.copiedBuffer(validPacket);

        channel.writeOneInbound(buf).sync();

        EMVTransmission validTransmission = channel.readInbound();
        assertEquals(27, validTransmission.dataLen());
        assertEquals(1, validTransmission.messages().length);

        EMVMessage validMessage = validTransmission.messages()[0];

        assertEquals(4, validMessage.tags().length);

        assertTrue(Arrays.stream(validMessage.tags()).anyMatch(tag -> tag.tagId() == EMVTagId.PAN));
        assertTrue(Arrays.stream(validMessage.tags()).anyMatch(tag -> tag.tagId() == EMVTagId.KERNEL_IDENTIFIER));
        assertTrue(Arrays.stream(validMessage.tags()).anyMatch(tag -> tag.tagId() == EMVTagId.AMOUNT_AUTHORISED));
        assertTrue(Arrays.stream(validMessage.tags()).anyMatch(tag -> tag.tagId() == EMVTagId.TRANSACTION_CURRENCY_CODE));

        assertDoesNotThrow(channel::checkException);
    }

    @Test
    public void testMissingStartOfMessage() throws InterruptedException {
        byte[] validPacket = new byte[]{
            0x00, 0x1B, 0x03, 0x18, (byte) 0x9F, 0x2A, 0x01, 0x02,
            (byte) 0x9F, 0x02, 0x02, 0x01, 0x00, 0x5A, 0x08, 0x41,
            0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x5F,
            0x2A, 0x02, 0x09, 0x78, 0x03
        };

        EmbeddedChannel channel = new EmbeddedChannel(
            new EMVBytesDecoder(),
            new EMVTransmissionDecoder()
        );

        ByteBuf buf = Unpooled.copiedBuffer(validPacket);

        Exception exp = assertThrows(ServerPipelineException.class, () -> channel.writeInbound(buf));

        assertEquals("EMVParser received message with invalid STX byte: 3", exp.getMessage());
    }

    @Test
    public void testMissingEndOfMessage() throws InterruptedException {
        byte[] validPacket = new byte[]{
            0x00, 0x1A, 0x02, 0x18, (byte) 0x9F, 0x2A, 0x01, 0x02,
            (byte) 0x9F, 0x02, 0x02, 0x01, 0x00, 0x5A, 0x08, 0x41,
            0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x5F,
            0x2A, 0x02, 0x09, 0x78
        };

        EmbeddedChannel channel = new EmbeddedChannel(
            new EMVBytesDecoder(),
            new EMVTransmissionDecoder()
        );

        ByteBuf buf = Unpooled.copiedBuffer(validPacket);

        Exception exp = assertThrows(ServerPipelineException.class, () -> channel.writeInbound(buf));

        assertEquals("EMVParser didn't receive ETX byte", exp.getMessage());
    }

    @Test
    public void testFailedTagValidation() throws InterruptedException {
        byte[] validPacket = new byte[]{
            0x00, 0x1B, 0x02, 0x18, (byte) 0x9F, 0x2A, 0x01, (byte) 0x02,
            (byte) 0x9F, 0x02, 0x02, 0x01, 0x00, 0x5A, 0x08, 0x41,
            0x11, 0x11, 0x1F, 0x11, 0x11, 0x11, 0x11, 0x5F,
            0x2A, 0x02, 0x09, 0x78, 0x03
        };

        EmbeddedChannel channel = new EmbeddedChannel(
            new LoggingHandler(true),
            new EMVBytesDecoder(),
            new EMVTransmissionDecoder()
        );

        ByteBuf buf = Unpooled.copiedBuffer(validPacket);

        Exception exp = assertThrows(ServerPipelineException.class, () -> channel.writeInbound(buf));

        assertEquals("EMVParser received invalid tag value for tag PAN", exp.getMessage());
    }
}
