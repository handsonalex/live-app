package org.live.msg.provider.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.live.framework.redis.starter.key.MsgProviderCacheKeyBuilder;
import org.live.msg.dto.MsgCheckDTO;
import org.live.msg.enums.MsgSendResultEnum;
import org.live.msg.provider.config.ThreadPoolManager;
import org.live.msg.provider.dao.mapper.SmsMapper;
import org.live.msg.provider.dao.po.SmsPO;
import org.live.msg.provider.service.ISmsService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SmsServiceImpl implements ISmsService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private MsgProviderCacheKeyBuilder msgProviderCacheKeyBuilder;

    @Resource
    private SmsMapper smsMapper;

    @Override
    public MsgSendResultEnum sendLoginCode(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return MsgSendResultEnum.MSG_PARAM_ERROR;
        }
        //生成验证码,4位、6位,有效期(30s,60s),同一个手机号不能重复发，redis去存储验证码
        String codeCacheKey = msgProviderCacheKeyBuilder.buildSmsLoginCodeKey(phone);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(codeCacheKey))){
            log.warn("改手机号短信发送过于频繁,phone is {}",phone);
            return MsgSendResultEnum.SEND_FAIL;
        }
        int code = RandomUtils.nextInt(100000,999999);
        redisTemplate.opsForValue().set(codeCacheKey,code,60, TimeUnit.SECONDS);
        //发送验证码
        ThreadPoolManager.commonAsyncPool.execute(() -> {
            boolean sendStatus = mockSendSms(phone,code);
            if (sendStatus){
                insertOne(phone, code);
            }
        });
        //插入验证码发送记录
        return MsgSendResultEnum.SEND_SUCCESS;
    }


    @Override
    public MsgCheckDTO checkLoginCode(String phone, Integer code) {
        //参数校验
        if (StringUtils.isEmpty(phone) || code == null || code < 100000){
            return new MsgCheckDTO(false,"参数异常");
        }
        //redis校验验证码
        String codeCacheKey = msgProviderCacheKeyBuilder.buildSmsLoginCodeKey(phone);
        Integer cacheCode = (Integer) redisTemplate.opsForValue().get(codeCacheKey);
        if (cacheCode == null || cacheCode < 100000){
            return new MsgCheckDTO(false, "验证码已过期");
        }
        if (cacheCode.equals(code)){
            redisTemplate.delete(codeCacheKey);
            return new MsgCheckDTO(true, "验证码校验成功");
        }
        return new MsgCheckDTO(false, "验证码校验失败");
    }

    @Override
    public void insertOne(String phone, Integer code) {
        SmsPO smsPO = new SmsPO();
        smsPO.setPhone(phone);
        smsPO.setCode(code);
        smsMapper.insert(smsPO);
    }

    private boolean mockSendSms(String phone, Integer code) {
        try {
            log.info("================== 创建短信发送通道中 ====================,phone is {},code is {}",phone,code);
            Thread.sleep(1000);
            log.info("================== 短信发送已成功 ===================");
            return true;
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }
}
