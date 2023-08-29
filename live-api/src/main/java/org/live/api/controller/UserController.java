package org.live.api.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.live.user.dto.UserDTO;
import org.live.user.interfaces.IUserRpc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author :Joseph Ho
 * Description:
 * Date: 0:34 2023/8/23
 */

@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 如果服务端设置了相关参数，调用端也要设置，不然请求不会分过来
     * 可以设置特定参数让特定的链路请求调用，比如设置url参数
     * DubboReference(group = "test"，url=“127.0.0.1”)
     * 详情看文档
     */
    @DubboReference
    private IUserRpc userRpc;


    @GetMapping("/batchQueryUserInfo")
    public Map<Long,UserDTO> batchQueryUserInfo(String userIdStr){
        return userRpc.batchQueryUserInfo(Arrays.stream(userIdStr.split(",")).map(Long::valueOf).toList());
    }

    @GetMapping("/getUserId")
    public UserDTO getUserId(Long userId){
        return userRpc.getByUserId(userId);
    }

    @GetMapping("/updateUserInfo")
    public boolean getUserId(Long userId,String nickname){
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setNickName(nickname);
        return userRpc.updateUserInfo(userDTO);
    }

    @GetMapping("/insertOne")
    public boolean insertOne(Long userId){
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setNickName("idea-test");
        userDTO.setSex(1);
        return userRpc.insertOne(userDTO);
    }
}
