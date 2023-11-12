package org.live.im.router.provider.service.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.live.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import org.live.im.dto.ImMsgBody;
import org.live.im.router.provider.service.ImRouterService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ImRouterServiceImpl implements ImRouterService {

    @DubboReference
    private IRouterHandlerRpc routerHandlerRpc;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean sendMsg(Long objectId, ImMsgBody imMsgBody) {

        String bindAddress = stringRedisTemplate.opsForValue().get(ImCoreServerConstants.IM_BIND_IP_KEY + imMsgBody.getAppId() + objectId);
        if (StringUtils.isEmpty(bindAddress)){
            return false;
        }
        String objectImServerIp = "192.168.31.105:9099";
        RpcContext.getContext().set("ip",objectImServerIp);
        routerHandlerRpc.sendMsg(imMsgBody);
        return true;
    }
}
