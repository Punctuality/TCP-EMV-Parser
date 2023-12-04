package site.sergeyfedorov.emv.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.sergeyfedorov.emv.exceptions.ServerLifecycleException;
import site.sergeyfedorov.emv.server.handler.*;
import site.sergeyfedorov.emv.server.handler.decoder.EMVBytesDecoder;
import site.sergeyfedorov.emv.server.handler.decoder.EMVTransmissionDecoder;

public class EMVServer {
    private static final Logger logger = LoggerFactory.getLogger(EMVServer.class);

    private final int listeningPort;
    private final ChannelInboundHandlerAdapter[] userHandlers;

    public EMVServer(int listeningPort, ChannelInboundHandlerAdapter... userHandlers) {
        this.listeningPort = listeningPort;
        this.userHandlers = userHandlers;
    }

    public EMVServer(int listeningPort) {
        this(listeningPort, new ChannelInboundHandlerAdapter[0]);
    }

    private ServerBootstrap setupServer() {

        ServerBootstrap b = new ServerBootstrap();
        b.group(
                EMVServerNettyGroups.getGroups().getBossGroup(),
                EMVServerNettyGroups.getGroups().getWorkerGroup()
            )
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                        new LoggingHandler(true),
                        new EMVBytesDecoder(),
                        new EMVTransmissionDecoder());

                    ch.pipeline().addLast(userHandlers);

                    ch.pipeline().addLast(
                        new RespondingHandler(),
                        new CleaningHandler()
                    );
                }
            }).option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true);

        return b;
    }

    public void run() throws InterruptedException {
        ServerBootstrap serverTemplate = setupServer();

        ChannelFuture f = serverTemplate.bind(this.listeningPort).addListener(future -> {
            if (future.isSuccess()) {
                logger.info("Server started on port " + this.listeningPort);
            } else {
                String msg = "Failed to start server on port " + this.listeningPort;
                logger.error(msg);
                throw new ServerLifecycleException(msg, future.cause());
            }
        }).sync();

        f.channel().closeFuture().sync();
    }
}
