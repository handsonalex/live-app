package org.live.user.provider.service;

import org.live.user.dto.UserLoginDTO;
import org.live.user.dto.UserPhoneDTO;

import java.util.List;


public interface IUserPhoneService {

    /**
     * 用户登录（底层会进行手机号注册）
     * @param phone
     * @return
     */
    UserLoginDTO login(String phone);

    UserPhoneDTO queryByPhone(String phone);

    /**
     * 根据id查询手机相关信息
     * @param userId
     * @return
     */
    List<UserPhoneDTO> queryByUserId(Long userId);
}
