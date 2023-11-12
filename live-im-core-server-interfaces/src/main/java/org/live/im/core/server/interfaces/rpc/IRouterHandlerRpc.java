package org.live.im.core.server.interfaces.rpc;

import org.live.im.dto.ImMsgBody;

public interface IRouterHandlerRpc {

    /**
     * 按用户id进行消息发送
     * @param imMsgBody
     */
    void sendMsg(ImMsgBody imMsgBody);
}
