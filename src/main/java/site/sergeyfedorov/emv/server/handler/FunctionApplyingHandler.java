package site.sergeyfedorov.emv.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import site.sergeyfedorov.emv.exceptions.ServerPipelineException;

import java.util.function.Function;

@ChannelHandler.Sharable
public class FunctionApplyingHandler<In, Out> extends ChannelInboundHandlerAdapter {

    private final Function<In, Out> function;
    private final Class<In> classToken;

    public FunctionApplyingHandler(Function<In, Out> function, Class<In> classToken) {
        this.function = function;
        this.classToken = classToken;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg.getClass().equals(classToken)) {
            Out out = function.apply(classToken.cast(msg));
            ctx.fireChannelRead(out);
        } else {
            ctx.fireExceptionCaught(new ServerPipelineException(
                String.format("Function expected %s, got %s", classToken.getSimpleName(), msg.getClass().getSimpleName())
            ));
        }
    }
}
