package org.live.api.controller;

import org.live.api.vo.TestVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/testPost")
    public String testPost(String id){
        System.out.println("id is:" + id);
        return "post";
    }

    @PostMapping("/testPost2")
    public String testPost2(TestVO testVO){
        System.out.println("id is:" + testVO.getId());
        return "post";
    }
}
