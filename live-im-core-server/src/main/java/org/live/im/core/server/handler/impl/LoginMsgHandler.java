package org.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.live.im.core.server.common.ImMsg;
import org.live.im.core.server.handler.SimplyHandler;

public class LoginMsgHandler implements SimplyHandler {
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        System.out.println("[login]:" + imMsg);
        ctx.writeAndFlush(imMsg);
    }
}
