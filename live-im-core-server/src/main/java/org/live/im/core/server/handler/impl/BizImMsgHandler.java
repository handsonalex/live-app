package org.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.live.im.core.server.common.ImContextUtils;
import org.live.im.core.server.common.ImMsg;
import org.live.im.core.server.handler.SimplyHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BizImMsgHandler implements SimplyHandler {

    @Resource
    private MQProducer mqProducer;

    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        //前期参数校验
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if (userId == null || appId == null) {
            log.error("attr error,imMsg is {}",imMsg);
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        byte[] body = imMsg.getBody();
        if (body == null || body.length == 0){
            log.error("body error,imMsg is {}",imMsg);
            return;
        }
        Message message = new Message();
        message.setTopic(ImCoreServerProviderTopicNames.LIVE_IM_BIZ_MSG_TOPIC);
        message.setBody(body);
        try {
            SendResult sendResult = mqProducer.send(message);
            log.info("[BizImMsgHandler] 消息投递结果:{}",sendResult);
        } catch (Exception e) {
            log.error("send error,error is: ",e);
            throw new RuntimeException(e);
        }
    }
}
