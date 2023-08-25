package org.live.user.provider.service;

import org.live.user.dto.UserDTO;

public interface IUserService {

    UserDTO getByUserId(Long userId);

    /**
     * 用户更新
     * @param userDTO
     * @return
     */
    boolean updateUserInfo(UserDTO userDTO);

    /**
     * 插入用户信息
     * @param userDTO
     * @return
     */
    boolean insertOne(UserDTO userDTO);

}
