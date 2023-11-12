package org.live.im.core.server.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import org.live.im.core.server.service.IRouterHandlerService;
import org.live.im.dto.ImMsgBody;

@DubboService
public class RouterHandlerRpcImpl implements IRouterHandlerRpc {
    @Resource
    private IRouterHandlerService routerHandlerService;
    @Override
    public void sendMsg(ImMsgBody imMsgBody) {
        routerHandlerService.onReceive(imMsgBody);
    }
}
