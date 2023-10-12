package org.live.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.live.im.constants.AppIdEnum;
import org.live.im.constants.ImMsgCodeEnum;
import org.live.im.core.server.common.ChannelHandlerContextCache;
import org.live.im.core.server.common.ImContextUtils;
import org.live.im.core.server.common.ImMsg;
import org.live.im.core.server.handler.SimplyHandler;
import org.live.im.dto.ImMsgBody;
import org.live.im.interfaces.ImTokenRpc;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class LoginMsgHandler implements SimplyHandler {

    @DubboReference
    private ImTokenRpc imTokenRpc;

    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        //防止重复请求
        if(ImContextUtils.getUserId(ctx) != null){
            return;
        }
        byte[] body = imMsg.getBody();
        if (body == null || body.length == 0){
            ctx.close();
            log.error("body error,imMsg is {}",imMsg);
            throw new IllegalArgumentException("body error");
        }
        ImMsgBody imMsgBody = JSON.parseObject(new String(body),ImMsgBody.class);
        Long userIdFromMsg = imMsgBody.getUserId();
        Integer appId = imMsgBody.getAppId();
        String token = imMsgBody.getToken();
        if (StringUtils.isEmpty(token) || userIdFromMsg < 10000 || appId < 10000){
            ctx.close();
            log.error("param error,imMsg is {}",imMsg);
            throw new IllegalArgumentException("param error");
        }
        Long userId = imTokenRpc.getUserIdByToken(token);
        //token校验成功
        if (userId != null && userId.equals(userIdFromMsg)){
            //按照userId保存好相关的channel对象信息
            ChannelHandlerContextCache.put(userId, ctx);
            ImContextUtils.setUserId(ctx, userId);
            ImContextUtils.setAppId(ctx, appId);
            //将im消息回写给客户端
            ImMsgBody respBody = new ImMsgBody();
            respBody.setAppId(appId);
            respBody.setUserId(userId);
            respBody.setData("true");
            ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(respBody));
            log.info("{LoginMsgHandler} login success,userId is {},appId is {}",userId,appId);
            ctx.writeAndFlush(respMsg);
            return;
        }
        ctx.close();
        log.error("body error,imMsg is {}",imMsg);
        throw new IllegalArgumentException("token check error");

    }
}
