package org.live.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.live.im.constants.ImMsgCodeEnum;
import org.live.im.core.server.common.ChannelHandlerContextCache;
import org.live.im.core.server.common.ImContextUtils;
import org.live.im.core.server.common.ImMsg;
import org.live.im.core.server.handler.SimplyHandler;
import org.live.im.dto.ImMsgBody;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogoutMsgHandler implements SimplyHandler {
    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if (userId == null || appId == null) {
            log.error("attr error,imMsg is {}",imMsg);
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        //将im消息写回给客户端
        ImMsgBody respBody = new ImMsgBody();
        respBody.setAppId(appId);
        respBody.setUserId(userId);
        respBody.setData("true");
        ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), JSON.toJSONString(respBody));
        ctx.writeAndFlush(respMsg);
        log.info("[LogoutMsgHandler] logout success,userId is {},appId is {}",userId,appId);
        //理想情况下，客户端断线的时候，会发送一个断线的消息包
        ChannelHandlerContextCache.remove(userId);
        ImContextUtils.removeUserId(ctx);
        ImContextUtils.removeAppId(ctx);
        ctx.close();
    }
}
