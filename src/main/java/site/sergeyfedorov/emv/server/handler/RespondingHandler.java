package site.sergeyfedorov.emv.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class RespondingHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String response = msg.toString().stripTrailing() + "\n";
        ByteBuf responseBuf = ctx.alloc().buffer(response.length());
        responseBuf.writeBytes(response.getBytes(StandardCharsets.UTF_8));

        ctx.writeAndFlush(responseBuf);

        ctx.fireChannelRead(msg);
    }
}
