package org.live.user.provider.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author :Joseph Ho
 * Description: 生产者配置信息
 * Date: 16:18 2023/8/29
 */
@ConfigurationProperties(prefix = "app.rmq.producer")
@Configuration
@Data
public class RocketMQProducerProperties {

    /**
     * nameSever地址
     */
    private String nameSrv;
    /**
     * 分组名称
     */
    private String groupName;

    private String brokerIp;

    private String applicationName;

    /**
     * 消息重发次数
     */
    private int retryTime;

    private int sendTimeOut;

}
