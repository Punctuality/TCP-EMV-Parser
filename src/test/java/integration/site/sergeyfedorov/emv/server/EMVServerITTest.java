package integration.site.sergeyfedorov.emv.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import site.sergeyfedorov.emv.model.EMVTransmission;
import site.sergeyfedorov.emv.server.EMVServer;
import site.sergeyfedorov.emv.server.handler.FunctionApplyingHandler;
import site.sergeyfedorov.transaction.TransactionsParser;
import site.sergeyfedorov.transaction.model.Transaction;
import site.sergeyfedorov.transaction.repr.TransactionBeautifier;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EMVServerITTest {
    private static final EMVServer server = new EMVServer(18123,
        new FunctionApplyingHandler<>(new TransactionsParser(), EMVTransmission.class),
        new FunctionApplyingHandler<>(
            transactions -> Arrays
                .stream(transactions)
                .map(new TransactionBeautifier(true))
                .reduce((a, b) -> a + "\n" + b)
                .orElse(""),
            Transaction[].class
        ));
    private static ChannelFuture runningServerFuture;

    @BeforeAll
    public static void setUp() throws InterruptedException {
        runningServerFuture = server.run();
    }

    @AfterAll
    public static void tearDown() {
        runningServerFuture.cancel(true);
    }

    static class TestHandler extends ChannelInboundHandlerAdapter {
        private final AtomicReference<String> placeholder;

        private ByteBuf receivedData;

        TestHandler(AtomicReference<String> placeholder) {
            this.placeholder = placeholder;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            if (msg instanceof ByteBuf) {
                ByteBuf data = (ByteBuf) msg;
                receivedData = data.copy();
            }
            placeholder.set(receivedData.toString(StandardCharsets.UTF_8));
            ctx.close();
        }

        public ByteBuf getReceivedData() throws InterruptedException {
            return receivedData;
        }
    }

    @Test
    public void testSampleEMVTransmission() throws InterruptedException {
        byte[] validPacket = new byte[]{
            0x00, 0x1B, 0x02, 0x18, (byte) 0x9F, 0x2A, 0x01, 0x02,
            (byte) 0x9F, 0x02, 0x02, 0x01, 0x00, 0x5A, 0x08, 0x41,
            0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x11, 0x5F,
            0x2A, 0x02, 0x09, 0x78, 0x03
        };
        String expected = """
            Transaction {
                kernel: MasterCard
                cardNumber: 411111******1111 (16 digits)
                amount: 1.00
                currency: Euro (EUR)
            }
            """;

        AtomicReference<String> placeholder = new AtomicReference<>();

        String host = "localhost";
        int port = 18123;

        try {
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new TestHandler(placeholder));
                    }
                });
            ChannelFuture f = bootstrap.connect(host, port).sync();

            Channel channel = f.channel();
            ByteBuf dataToSend = Unpooled.copiedBuffer(validPacket);
            channel.writeAndFlush(dataToSend).sync();

            // Not the best, but was already running out of time for the task
            Thread.sleep(200);

            assertEquals(expected, placeholder.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
