package org.live.im.core.server.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.live.im.constants.ImConstants;

import java.util.List;

public class ImMsgDecoder extends ByteToMessageDecoder {
    private final int BASE_LEN = 2 + 4 + 4;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        //bytebuf内容的基本校验，长度校验，magic校验
        if (byteBuf.readableBytes() >= BASE_LEN){
            if (byteBuf.readShort() != ImConstants.DEFAULT_MAGIC){
                ctx.close();
                return;
            }
            int code = byteBuf.readInt();
            int len = byteBuf.readInt();
            //确保bytebuf剩余队消息长度足够
            if (byteBuf.readableBytes() < len){
                ctx.close();
                return;
            }
            byte[] body = new byte[len];
            byteBuf.readBytes(body);
            //将bytebuf转换为imMsg对象
            ImMsg imMsg = new ImMsg();
            imMsg.setCode(code);
            imMsg.setLen(len);
            imMsg.setMagic(ImConstants.DEFAULT_MAGIC);
            imMsg.setBody(body);
            out.add(imMsg);
        }
        //bytebuf转换为imMsg对象

    }
}
