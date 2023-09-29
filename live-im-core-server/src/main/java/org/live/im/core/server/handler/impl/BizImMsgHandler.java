package org.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.live.im.core.server.common.ImMsg;
import org.live.im.core.server.handler.SimplyHandler;

public class BizImMsgHandler implements SimplyHandler {
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        System.out.println("[biz]: " + imMsg);
        ctx.writeAndFlush(imMsg);
    }
}
