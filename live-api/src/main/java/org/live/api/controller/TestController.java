package org.live.api.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.live.user.interfaces.IUserRpc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :Joseph Ho
 * Description:
 * Date: 0:34 2023/8/23
 */

@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * 如果服务端设置了相关参数，调用端也要设置，不然请求不会分过来
     * 可以设置特定参数让特定的链路请求调用，比如设置url参数
     * DubboReference(group = "test"，url=“127.0.0.1”)
     * 详情看文档
     */
    @DubboReference(group = "test")
    private IUserRpc userRpc;

    @GetMapping("dubbo")
    public String doTest(){
        return userRpc.test();
    }
}
