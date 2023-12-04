package site.sergeyfedorov.emv.server.handler.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;
import site.sergeyfedorov.emv.model.EMVRawTransfer;
import site.sergeyfedorov.emv.server.handler.FunctionApplyingHandler;
import site.sergeyfedorov.emv.server.handler.RespondingHandler;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EMVBytesDecoderTest {
    @Test
    public void testValidPacketDecoding() throws InterruptedException {
        EmbeddedChannel channel = new EmbeddedChannel(
            new EMVBytesDecoder()
        );

        byte[] validPacket = new byte[]{0x00, 0x02, 0x02, 0x03};
        ByteBuf buf = Unpooled.copiedBuffer(validPacket);

        channel.writeOneInbound(buf).sync();

        channel.checkException();

        EMVRawTransfer validTransfer = channel.readInbound();
        assertEquals(2, validTransfer.messageLength());
        assertEquals(2, validTransfer.rawMessage().readableBytes());
        assertEquals(0x02, validTransfer.rawMessage().readByte());
        assertEquals(0x03, validTransfer.rawMessage().readByte());
    }

    @Test
    public void testInvalidPacketDecoding() throws InterruptedException {
        EmbeddedChannel channel = new EmbeddedChannel(
            new EMVBytesDecoder()
        );

        byte[] validPacket = new byte[]{0x00, 0x03, 0x02, 0x03};
        ByteBuf buf = Unpooled.copiedBuffer(validPacket);

        channel.writeOneInbound(buf).sync();

        ByteBuf resultInbound = channel.readInbound();
        assertEquals(null, resultInbound);
    }
}
