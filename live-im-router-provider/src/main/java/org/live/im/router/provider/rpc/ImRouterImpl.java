package org.live.im.router.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.live.im.dto.ImMsgBody;
import org.live.im.router.interfaces.rpc.ImRouterRpc;
import org.live.im.router.provider.service.ImRouterService;


@DubboService
public class ImRouterImpl implements ImRouterRpc {

    @Resource
    private ImRouterService imRouterService;

    @Override
    public boolean sendMsg(Long objectId, ImMsgBody imMsgBody) {
        return imRouterService.sendMsg(objectId,imMsgBody);
    }
}
