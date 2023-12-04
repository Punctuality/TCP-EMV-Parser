package site.sergeyfedorov.emv.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RespondingHandlerTest {
    @Test
    public void testApplyingFunction() throws InterruptedException {
        EmbeddedChannel channel = new EmbeddedChannel(
            new FunctionApplyingHandler<>(bb -> bb.toString(StandardCharsets.UTF_8), ByteBuf.class),
            new RespondingHandler(),
            new FunctionApplyingHandler<>(String::length, String.class),
            new RespondingHandler()
        );

        ByteBuf buf = Unpooled.copiedBuffer("test", StandardCharsets.US_ASCII);

        channel.writeOneInbound(buf).sync();

        Integer resultInbound = channel.readInbound();
        assertEquals(buf.readableBytes(), resultInbound);

        ByteBuf resultOutbound1 = channel.readOutbound();
        assertEquals("test\n", resultOutbound1.toString(StandardCharsets.UTF_8));

        ByteBuf resultOutbound2 = channel.readOutbound();
        assertEquals(buf.readableBytes() + "\n", resultOutbound2.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void testCatchingError() throws InterruptedException {
        String testErrMsg = "Test Exception";
        EmbeddedChannel channel = new EmbeddedChannel(
            new FunctionApplyingHandler<>(bb -> {
                throw new RuntimeException(testErrMsg);
            }, ByteBuf.class),
            new RespondingHandler()
        );

        ByteBuf buf = Unpooled.copiedBuffer("test", StandardCharsets.US_ASCII);

        channel.writeOneInbound(buf).sync();

        ByteBuf resultOutbound1 = channel.readOutbound();
        assertEquals("Processing Error: %s\n".formatted(testErrMsg), resultOutbound1.toString(StandardCharsets.UTF_8));
    }
}
