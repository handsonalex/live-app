package org.live.msg.provider;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomUtils;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.live.msg.dto.MsgCheckDTO;
import org.live.msg.enums.MsgSendResultEnum;
import org.live.msg.provider.service.ISmsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

/**
 * @Author idea
 * @Date: Created in 17:21 2023/6/11
 * @Description
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
public class MsgProviderApplication implements CommandLineRunner {

    @Resource
    private ISmsService smsService;
    public static void main(String[] args) {
        SpringApplication springApplication = new
        SpringApplication(MsgProviderApplication.class);

        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
