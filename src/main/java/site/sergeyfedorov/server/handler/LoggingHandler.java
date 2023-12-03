package site.sergeyfedorov.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingHandler extends ChannelInboundHandlerAdapter {

    private final boolean displayContents;

    private static final Logger logger = LoggerFactory.getLogger(LoggingHandler.class);

    public LoggingHandler(Boolean displayContents) {
        this.displayContents = displayContents;
    }

    private String convertByteToHex(byte b) {
        return String.format("%02X", b);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String logMessage = String.format("Received message: %s",
            displayContents ? msg : msg.getClass().getSimpleName()
        );

        if (msg instanceof ByteBuf bb) {
            logMessage += String.format(" (%s bytes)", bb.readableBytes());

            if (displayContents) {
                bb.markReaderIndex();
                try {
                    StringBuilder sb = new StringBuilder(4 + bb.readableBytes() * 2);
                    sb.append(": 0x");
                    while (bb.isReadable()) {
                        sb.append(convertByteToHex(bb.readByte()));
                    }

                    logMessage += sb.toString();
                } finally {
                    bb.resetReaderIndex();
                }
            }
        }

        logger.info(logMessage);

        ctx.fireChannelRead(msg);
    }
}
