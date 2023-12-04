package site.sergeyfedorov.emv.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoggingHandlerTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void checkLoggedContentsMessage() throws InterruptedException {
        EmbeddedChannel channel = new EmbeddedChannel(
            new LoggingHandler(true),
            new CleaningHandler()
        );

        ByteBuf buf = Unpooled.copiedBuffer("test", StandardCharsets.US_ASCII);
        String filteringRegex = "^.+LoggingHandler.+Received message.+%s.+\\(%d bytes\\).+0x\\d{%d}$".formatted(
            buf.getClass().getSimpleName(),
            buf.readableBytes(),
            buf.readableBytes() * 2
        );

        channel.writeOneInbound(buf).sync();

        Optional<String> loggedLine =
            outContent.toString().lines().filter(outLine -> outLine.matches(filteringRegex)).findAny();
        assertTrue(loggedLine.isPresent());
    }

    @Test
    public void checkLoggedShortMessage() throws InterruptedException {
        EmbeddedChannel channel = new EmbeddedChannel(
            new LoggingHandler(false),
            new CleaningHandler()
        );

        ByteBuf buf = Unpooled.copiedBuffer("test", StandardCharsets.US_ASCII);
        String filteringRegex = "^.+LoggingHandler.+Received message.+%s.+\\(%d bytes\\)$".formatted(
            buf.getClass().getSimpleName(),
            buf.readableBytes()
        );

        channel.writeOneInbound(buf).sync();

        Optional<String> loggedLine =
            outContent.toString().lines().filter(outLine -> outLine.matches(filteringRegex)).findAny();
        assertTrue(loggedLine.isPresent());
    }
}
