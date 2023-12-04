package site.sergeyfedorov.emv.server.handler.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import site.sergeyfedorov.emv.model.EMVRawTransfer;

import java.util.List;

public class EMVBytesDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.isReadable() && byteBuf.readableBytes() >= 2) {
            byteBuf.markReaderIndex();
            int length = byteBuf.readUnsignedShort();
            if (byteBuf.readableBytes() >= length) {
                list.add(new EMVRawTransfer(length, byteBuf.readBytes(length)));
            } else {
                byteBuf.resetReaderIndex();
            }
        }
    }
}
