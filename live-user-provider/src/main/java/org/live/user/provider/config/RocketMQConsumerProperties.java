package org.live.user.provider.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app.rmq.consumer")
@Configuration
@Data
public class RocketMQConsumerProperties {
    /**
     * nameSever地址
     */
    private String nameSrv;
    /**
     * 分组名称
     */
    private String groupName;

    private String brokerIp;
}
