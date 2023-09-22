package org.live.api.controller;

import org.live.common.interfaces.vo.WebResponseVO;
import org.live.web.starter.RequestContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomePageController {

    @PostMapping("/initPage")
    public WebResponseVO initPage(){
        Long userId = RequestContext.getUserId();
//        new Thread(() -> RequestContext.getUserId()).start();
        System.out.println(userId);
        //前端调用initPage --> success状态，代表登录过了，token依旧有效
        return WebResponseVO.success();
    }
}
