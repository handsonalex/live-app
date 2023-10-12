package org.live.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import org.live.im.constants.ImConstants;
import org.live.im.constants.ImMsgCodeEnum;
import org.live.im.core.server.common.ImContextUtils;
import org.live.im.core.server.common.ImMsg;
import org.live.im.core.server.handler.SimplyHandler;
import org.live.im.dto.ImMsgBody;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class HeartBeatMsgHandler implements SimplyHandler {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        //心跳包基本校验
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if (userId == null || appId == null) {
            log.error("attr error,imMsg is {}",imMsg);
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        //心跳包record记录，redis存储
        String redisKey = cacheKeyBuilder.buildImLoginTokenKey(userId,appId);
        //redis里数据存储格式 live-im-core-server:heartbeat:999:zset
        this.recordOnlineTime(userId,redisKey);
        //zSet集合存储心跳记录，基于userId取模，ket(userId)-score(心跳时间)
        this.removeExpireRecord(redisKey);
        redisTemplate.expire(redisKey, 5, TimeUnit.MINUTES);
        ImMsgBody msgBody = new ImMsgBody();
        msgBody.setUserId(userId);
        msgBody.setAppId(appId);
        msgBody.setData("true");
        ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(msgBody));
        log.info("[HeartbeatImMsg] imMsg is {}",imMsg);
        ctx.writeAndFlush(respMsg);
    }

    /**
     * 清理掉过期不在线用户留下的心跳记录（在两次心跳包的发送间隔中，如果没有重新更新score值，就会导致被删除）
     * @param redisKey
     */
    private void removeExpireRecord(String redisKey) {
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, System.currentTimeMillis() - ImConstants.DEFAULT_HEART_BEAT_GAP * 1000 * 2);
    }

    /**
     * 记录用户最近一次心跳时间到zSet
     * @param userId
     * @param redisKey
     */
    private void recordOnlineTime(Long userId, String redisKey) {
        redisTemplate.opsForZSet().add(redisKey, userId, System.currentTimeMillis());
    }


}
