package org.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
//条件注入,按照RedisKeyLoadMatch里设定的规则加载类
@Conditional(RedisKeyLoadMatch.class)
public class MsgProviderCacheKeyBuilder extends RedisKeyBuilder{

    private final static String SMS_LOGIN_CODE_KEY = "smsLoginCode";
    public String buildSmsLoginCodeKey(String phone){
        return super.getPrefix() + SMS_LOGIN_CODE_KEY + super.getSplitItem() + phone;
    }

}
