package org.live.user.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.live.common.interfaces.utils.ConvertBeanUtils;
import org.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.live.user.dto.UserDTO;
import org.live.user.provider.dao.mapper.IUserMapper;
import org.live.user.provider.dao.po.UserPO;
import org.live.user.provider.service.IUserService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {
    @Resource
    private IUserMapper IUserMapper;

    @Resource
    private RedisTemplate<String,UserDTO> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Resource
    private MQProducer mqProducer;

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
        userDTO = ConvertBeanUtils.convert(IUserMapper.selectById(userId),UserDTO.class);
        if (userDTO != null){
            redisTemplate.opsForValue().set(key,userDTO,30,TimeUnit.MINUTES);
        }
        return userDTO;
    }

    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        IUserMapper.updateById(ConvertBeanUtils.convert(userDTO, UserPO.class));
        String key = userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId());
        //redis第一次删除
        redisTemplate.delete(key);
        Message message = new Message();
        message.setTopic("user-update-cache");
        message.setBody(JSON.toJSONString(userDTO).getBytes());
        //延迟级别，1代表延迟1秒发送
        message.setDelayTimeLevel(1);
        try {
            mqProducer.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public boolean insertOne(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        IUserMapper.insert(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }

    @Override
    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)){
            return Maps.newHashMap();
        }
        //id合法性判断
        userIdList = userIdList.stream().filter(id -> id > 10000).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIdList)){
            return Maps.newHashMap();
        }
        /*
        并发查询的思路，将userId分为多组，多线程查询，得到的结果再归并到一起
         */
        //redis 先查询
        List<String> keyList = new ArrayList<>();
        userIdList.forEach(userId -> {
            keyList.add(userProviderCacheKeyBuilder.buildUserInfoKey(userId));
        });
        List<UserDTO> userDTOList = new ArrayList<>(redisTemplate.opsForValue().multiGet(keyList).stream().filter(Objects::nonNull).toList());
        if (!CollectionUtils.isEmpty(userDTOList) && userDTOList.size() == userIdList.size()){
            return userDTOList.stream().collect(Collectors.toMap(UserDTO :: getUserId,x -> x));
        }
        List<Long> userIdInCacheList = userDTOList.stream().map(UserDTO :: getUserId).toList();
        List<Long> userIdNotInCacheList = userIdList.stream().filter(x -> !userIdInCacheList.contains(x)).toList();
        //mysql查询 多线程查询替换了union all
        Map<Long,List<Long>> userIdMap = userIdNotInCacheList.stream().collect(Collectors.groupingBy(userId -> userId % 100));
        // 多线程用CopyOnWriteArrayList
        List<UserDTO> dbQueryResult = new CopyOnWriteArrayList<>();
        userIdMap.values().parallelStream().forEach(queryUserIdList -> {
            dbQueryResult.addAll(ConvertBeanUtils.convertList(IUserMapper.selectBatchIds(queryUserIdList), UserDTO.class));
        });
        if (!CollectionUtils.isEmpty(dbQueryResult)){
            //根据业务具体需求判断要不要把mysql查出来的数据存入redis
            Map<String,UserDTO> saveCacheMap = dbQueryResult.stream().collect(
                    Collectors.toMap(userDto -> userProviderCacheKeyBuilder.buildUserInfoKey(userDto.getUserId()),x -> x));
            redisTemplate.opsForValue().multiSet(saveCacheMap);
            //管道批量传输命令，减少网络IO开销
            redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    for (String redisKey : saveCacheMap.keySet()) {
                        operations.expire((K)redisKey,createRandomExpireTime(),TimeUnit.SECONDS);
                    }
                    return null;
                }
            });
            userDTOList.addAll(dbQueryResult);
        }
        return userDTOList.stream().collect(Collectors.toMap(UserDTO :: getUserId,x -> x));
    }

    private int createRandomExpireTime(){
        int time = ThreadLocalRandom.current().nextInt(1000);
        return time + 60 * 30;
    }
}
