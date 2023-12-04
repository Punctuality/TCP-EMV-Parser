package site.sergeyfedorov.emv.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class RespondingHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String response = msg.toString().stripTrailing() + "\n";
        ByteBuf responseBuf = wrapString(response);

        ctx.writeAndFlush(responseBuf);

        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.writeAndFlush(wrapString("Processing Error: " + cause.getMessage() + "\n")).addListener(future -> {
            if (future.isSuccess()) {
                ctx.close();
            }
        });
    }

    private ByteBuf wrapString(String str) {
        return Unpooled.copiedBuffer(str, StandardCharsets.UTF_8);
    }
}
