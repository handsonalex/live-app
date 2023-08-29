package org.live.user.interfaces;

import org.live.user.dto.UserDTO;

import java.util.List;
import java.util.Map;

/**
 * @author :Joseph Ho
 * Description:
 * Date: 23:07 2023/8/22
 */
public interface IUserRpc {
    /**
     * 查询用户
     * @param userId
     * @return
     */
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

    Map<Long,UserDTO> batchQueryUserInfo(List<Long> userIdList);

}
