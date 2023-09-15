package org.live.user.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.live.common.interfaces.enums.CommonStatusEnum;
import org.live.common.interfaces.utils.ConvertBeanUtils;
import org.live.common.interfaces.utils.DESUtils;
import org.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.live.id.generate.enums.IdTypeEnum;
import org.live.id.generate.interfaces.IdGenerateRpc;
import org.live.user.dto.UserDTO;
import org.live.user.dto.UserLoginDTO;
import org.live.user.dto.UserPhoneDTO;
import org.live.user.provider.dao.mapper.IUserPhoneMapper;
import org.live.user.provider.dao.po.UserPO;
import org.live.user.provider.dao.po.UserPhonePO;
import org.live.user.provider.service.IUserPhoneService;
import org.live.user.provider.service.IUserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserPhoneServiceImpl implements IUserPhoneService {

    @Resource
    private IUserPhoneMapper userPhoneMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder cacheKeyBuilder;

    @Resource
    private IUserService userService;

    @DubboReference
    private IdGenerateRpc idGenerateRpc;

    @Override
    public UserLoginDTO login(String phone) {
        //phone不能为空
        if (StringUtils.isEmpty(phone)) {
            return null;
        }
        //是否注册过
        UserPhoneDTO userPhoneDTO = this.queryByPhone(phone);
        //如果注册过，创建token，返回userId
        if (userPhoneDTO != null) {
            return UserLoginDTO.loginSuccess(userPhoneDTO.getUserId(), createAndSaveLoginToken(userPhoneDTO.getUserId()));
        }
        //如果没注册过，生成user信息，插入手机记录，绑定userId
        return registerAndLogin(phone);
    }

    /**
     * 注册加登录
     * @param phone
     * @return
     */
    private UserLoginDTO registerAndLogin(String phone){
        Long userId = idGenerateRpc.getUnSeqId(IdTypeEnum.USER_ID.getCode());
        UserDTO userDTO = new UserDTO();
        userDTO.setNickName("berber用户-" + userId);
        userDTO.setUserId(userId);
        userService.insertOne(userDTO);
        UserPhonePO userPhonePO = new UserPhonePO();
        userPhonePO.setUserId(userId);
        userPhonePO.setPhone(DESUtils.encrypt(phone));
        userPhonePO.setStatus(CommonStatusEnum.VALID_STATUS.getCode());
        userPhoneMapper.insert(userPhonePO);
        redisTemplate.delete(cacheKeyBuilder.buildUserPhoneObjKey(phone));
        return UserLoginDTO.loginSuccess(userId, createAndSaveLoginToken(userId));
    }

    /**
     * 创建并且记录token
     * @param userId
     * @return
     */
    private String createAndSaveLoginToken(Long userId){
        String token = UUID.randomUUID().toString();
        String redisKey = cacheKeyBuilder.buildUserLoginTokenKey(token);
        redisTemplate.opsForValue().set(redisKey, userId, 30, TimeUnit.DAYS);
        return token;
    }

    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        if (StringUtils.isEmpty(phone)){
            return null;
        }
        String redisKey = cacheKeyBuilder.buildUserPhoneObjKey(phone);
        UserPhoneDTO userPhoneDTO = (UserPhoneDTO) redisTemplate.opsForValue().get(redisKey);
        if (userPhoneDTO != null) {
            //属于空值缓存对象
            if (userPhoneDTO.getUserId() == null){
                return null;
            }
            return userPhoneDTO;
        }
        userPhoneDTO = this.queryByPhoneFromDB(phone);
        if (userPhoneDTO != null) {
            redisTemplate.opsForValue().set(redisKey, userPhoneDTO,30,TimeUnit.MINUTES);
            return userPhoneDTO;
        }
        //缓存击穿，空值缓存
        userPhoneDTO = new UserPhoneDTO();
        redisTemplate.opsForValue().set(redisKey, userPhoneDTO,5,TimeUnit.MINUTES);
        return null;
    }

    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        if (userId == null || userId < 10000) {
            return Collections.emptyList();
        }
        String redisKey = cacheKeyBuilder.buildUserPhoneListKey(userId);
        List<Object> userPhoneList = redisTemplate.opsForList().range(redisKey,0,-1);
        if (!CollectionUtils.isEmpty(userPhoneList)){
            //空值缓存
            if (((UserPhoneDTO)userPhoneList.get(0)).getUserId() == null){
                return Collections.emptyList();
            }
            return userPhoneList.stream().map(x -> (UserPhoneDTO) x).collect(Collectors.toList());
        }
        List<UserPhoneDTO> userPhoneDTOS = this.queryByUserIdFromDB(userId);
        if (!CollectionUtils.isEmpty(userPhoneDTOS)){
            userPhoneDTOS.stream().forEach(x -> x.setPhone(DESUtils.encrypt(x.getPhone())));
            redisTemplate.opsForList().leftPushAll(redisKey,userPhoneDTOS.toArray());
            redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);
            return userPhoneDTOS;
        }
        //缓存击穿，空对象缓存
        redisTemplate.opsForList().leftPush(redisKey, new UserPhoneDTO());
        redisTemplate.expire(redisKey, 5, TimeUnit.MINUTES);
        return Collections.emptyList();
    }

    public List<UserPhoneDTO> queryByUserIdFromDB(Long userId){
        List<UserPhonePO> userPhonePO = userPhoneMapper.selectList(new LambdaQueryWrapper<UserPhonePO>()
                .eq(UserPhonePO::getUserId, userId).eq(UserPhonePO::getStatus, CommonStatusEnum.VALID_STATUS.getCode())
                .last("limit 1"));
        return ConvertBeanUtils.convertList(userPhonePO, UserPhoneDTO.class);
    }

    public UserPhoneDTO queryByPhoneFromDB(String phone){
        UserPhonePO userPhonePO = userPhoneMapper.selectOne(new LambdaQueryWrapper<UserPhonePO>()
                .eq(UserPhonePO::getPhone, DESUtils.encrypt(phone)).eq(UserPhonePO::getStatus, CommonStatusEnum.VALID_STATUS.getCode())
                .last("limit 1"));
        return ConvertBeanUtils.convert(userPhonePO, UserPhoneDTO.class);
    }
}
