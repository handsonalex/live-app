package org.live.user.provider;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.live.user.constants.UserTagsEnum;
import org.live.user.dto.UserDTO;
import org.live.user.dto.UserLoginDTO;
import org.live.user.provider.service.IUserPhoneService;
import org.live.user.provider.service.IUserService;
import org.live.user.provider.service.IUserTagService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.CountDownLatch;

/**
 * @author :Joseph Ho
 * Description: 用户中台服务提供者
 * Date: 23:01 2023/8/22
 */

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
@Slf4j
public class UserProviderApplication implements CommandLineRunner {

    @Resource
    private IUserTagService userTagService;

    @Resource
    private IUserService userService;

    @Resource
    private IUserPhoneService userPhoneService;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(UserProviderApplication.class);
        //该服务只是一个纯粹的netty的进程，用不到tomcat
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
//        String phone = "12476218471";
//        UserLoginDTO userLoginDTO = userPhoneService.login(phone);
//        System.out.println(userLoginDTO);
//        System.out.println(userPhoneService.queryByUserId(userLoginDTO.getUserId()));
//        System.out.println(userPhoneService.queryByUserId(userLoginDTO.getUserId()));
//        System.out.println(userPhoneService.queryByPhone(phone));
//        System.out.println(userPhoneService.queryByPhone(phone));
    }
}
