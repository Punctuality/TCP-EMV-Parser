package site.sergeyfedorov.emv.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionApplyingHandlerTest {
    @Test
    public void testApplyingFunction() throws InterruptedException {
        EmbeddedChannel channel = new EmbeddedChannel(
            new FunctionApplyingHandler<>(ByteBuf::readableBytes, ByteBuf.class)
        );

        ByteBuf buf = Unpooled.copiedBuffer("test", StandardCharsets.US_ASCII);

        channel.writeOneInbound(buf).sync();

        Integer result = channel.readInbound();
        assertEquals(buf.readableBytes(), result);
    }
}
