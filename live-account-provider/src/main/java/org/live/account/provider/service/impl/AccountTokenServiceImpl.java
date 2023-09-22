package org.live.account.provider.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.live.account.provider.service.IAccountTokenService;
import org.live.framework.redis.starter.key.AccountProviderCacheKeyBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AccountTokenServiceImpl implements IAccountTokenService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private AccountProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public String createAndSaveLoginToken(Long userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildUserLoginTokenKey(token), String.valueOf(userId),30, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getUserIdByToken(String tokenKey) {
        String redisKey = cacheKeyBuilder.buildUserLoginTokenKey(tokenKey);
        Integer userId = (Integer) redisTemplate.opsForValue().get(redisKey);
        return userId == null ? null : Long.valueOf(userId);
    }
}
