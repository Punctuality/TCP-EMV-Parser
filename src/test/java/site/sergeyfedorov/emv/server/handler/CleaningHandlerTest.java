package site.sergeyfedorov.emv.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CleaningHandlerTest {
    @Test
    public void testResourceRelease() throws InterruptedException {
        EmbeddedChannel channel = new EmbeddedChannel(new CleaningHandler());

        ByteBuf buf = Unpooled.buffer();
        assertEquals(1, buf.refCnt());

        channel.writeOneInbound(buf).sync();

        assertEquals(0, buf.refCnt());

        assertDoesNotThrow(channel::checkException);
    }
}
