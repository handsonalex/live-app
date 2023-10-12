package org.live.im.core.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author :Joseph Ho
 * Description: netty启动类
 * Date: 22:09 2023/9/24
 */
@SpringBootApplication
public class ImCoreServerApplication {


    public static void main(String[] args) throws InterruptedException {
        SpringApplication springApplication = new SpringApplication(ImCoreServerApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

}
