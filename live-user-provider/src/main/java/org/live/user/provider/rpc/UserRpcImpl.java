package org.live.user.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.live.user.dto.UserDTO;
import org.live.user.interfaces.IUserRpc;
import org.live.user.provider.service.IUserService;

/**
 * @author :Joseph Ho
 * Description:
 * Date: 23:07 2023/8/22
 */
@DubboService
public class UserRpcImpl implements IUserRpc {

    @Resource
    private IUserService userService;


    @Override
    public UserDTO getByUserId(Long userId) {
        return userService.getByUserId(userId);
    }

    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        return userService.updateUserInfo(userDTO);
    }

    @Override
    public boolean insertOne(UserDTO userDTO) {
        return userService.insertOne(userDTO);
    }
}
