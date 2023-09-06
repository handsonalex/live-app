package org.live.user.provider;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.live.user.constants.UserTagsEnum;
import org.live.user.dto.UserDTO;
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

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(UserProviderApplication.class);
        //该服务只是一个纯粹的netty的进程，用不到tomcat
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        Long userId = 1004L;
        UserDTO userDTO =userService.getByUserId(userId);
        userDTO.setNickName("test-nick-name");
        userService.updateUserInfo(userDTO);

        System.out.println(userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));
        System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_OLD_USER));
        System.out.println(userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));
        System.out.println(userTagService.cancelTag(userId, UserTagsEnum.IS_OLD_USER));
        System.out.println(userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));

//        System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_RICH));
//        System.out.println("当前用户是否拥有is_rich标签:" + userTagService.containTag(userId, UserTagsEnum.IS_RICH));
//        System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_VIP));
//        System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_VIP));
//        System.out.println("当前用户是否拥有is_vip标签:" + userTagService.containTag(userId, UserTagsEnum.IS_VIP));
//        System.out.println(userTagService.setTag(userId, UserTagsEnum.IS_OLD_USER));
//        System.out.println("当前用户是否拥有is_old_user标签:" + userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));
//
//        System.out.println("================================================");
//        System.out.println(userTagService.cancelTag(userId, UserTagsEnum.IS_RICH));
//        System.out.println("当前用户是否拥有is_rich标签:" + userTagService.containTag(userId, UserTagsEnum.IS_RICH));
//        System.out.println(userTagService.cancelTag(userId, UserTagsEnum.IS_VIP));
//        System.out.println("当前用户是否拥有is_vip标签:" + userTagService.containTag(userId, UserTagsEnum.IS_VIP));
//        System.out.println(userTagService.cancelTag(userId, UserTagsEnum.IS_OLD_USER));
//        System.out.println("当前用户是否拥有is_old_user标签:" + userTagService.containTag(userId, UserTagsEnum.IS_OLD_USER));
    }
}
