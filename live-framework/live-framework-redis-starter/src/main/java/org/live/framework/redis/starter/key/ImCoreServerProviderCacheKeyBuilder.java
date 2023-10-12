package org.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class ImCoreServerProviderCacheKeyBuilder extends RedisKeyBuilder{
    private static String IM_OLINE_ZSET = "imOnlineZset";

    /**
     * 按照用户id取模10000，得出具体缓存所在的key
     * @param userId
     * @return
     */
    public String buildImLoginTokenKey(Long userId,Integer appId){
        return super.getPrefix() + IM_OLINE_ZSET + super.getSplitItem() + appId + super.getSplitItem() + userId % 10000;
    }
}
