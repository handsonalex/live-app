package org.live.user.provider.service.impl;

import jakarta.annotation.Resource;
import org.live.common.interfaces.utils.ConvertBeanUtils;
import org.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.live.user.dto.UserDTO;
import org.live.user.provider.dao.mapper.UserMapper;
import org.live.user.provider.dao.po.UserPO;
import org.live.user.provider.service.IUserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String,UserDTO> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Override
    public UserDTO getByUserId(Long userId) {
        if (userId == null){
            return null;
        }
        String key = userProviderCacheKeyBuilder.buildUserInfoKey(userId);
        UserDTO userDTO = redisTemplate.opsForValue().get(key);
        if (userDTO != null){
            return userDTO;
        }
        userDTO = ConvertBeanUtils.convert(userMapper.selectById(userId),UserDTO.class);
        if (userDTO != null){
            redisTemplate.opsForValue().set(key,userDTO);
        }
        return userDTO;
    }

    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        userMapper.updateById(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }

    @Override
    public boolean insertOne(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        userMapper.insert(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }
}
