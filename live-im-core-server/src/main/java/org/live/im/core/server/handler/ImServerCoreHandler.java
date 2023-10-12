package org.live.im.core.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.live.im.core.server.common.ChannelHandlerContextCache;
import org.live.im.core.server.common.ImContextUtils;
import org.live.im.core.server.common.ImMsg;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class ImServerCoreHandler extends SimpleChannelInboundHandler {

    @Resource
    private  ImHandlerFactory imHandlerFactory;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ImMsg)){
            throw new IllegalArgumentException("error msg,msg is :" + msg);
        }
        ImMsg imMsg = (ImMsg) msg;
        imHandlerFactory.doMsgHandler(ctx, imMsg);
    }

    /**
     * 断线监听
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //如果之前是正常断线的话，上下文是不会有userId绑定信息的了
        Long userId = ImContextUtils.getUserId(ctx);
        if (userId != null) {
            ChannelHandlerContextCache.remove(userId);
        }
    }
}
