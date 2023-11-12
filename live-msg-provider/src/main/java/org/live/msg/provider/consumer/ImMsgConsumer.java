package org.live.msg.provider.consumer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ImMsgConsumer implements InitializingBean {

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    // 记录每个用户连接的im服务器地址，然后根据im服务器的连接地址去做具体机器调用
    // 基于mq广播思路去做，可能会有消息风暴，100台im机器，99%mq消息是无效的
    // 加入一个叫路由层的设计，router中转的设计，router就是一个dubbo的rpc层
    //A --> B  im-core-server -> msg-provider(对消息进行处理) -> im-core-server -> 通知B
    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + ImMsgConsumer.class.getSimpleName());
        mqPushConsumer.setConsumeMessageBatchMaxSize(10);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听im发送过来的业务消息topic
        mqPushConsumer.subscribe(ImCoreServerProviderTopicNames.LIVE_IM_BIZ_MSG_TOPIC, "");
         mqPushConsumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            String json = new String(msgs.get(0).getBody());
            System.out.println(json);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        mqPushConsumer.start();
        log.info("mq消费者启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }
}
