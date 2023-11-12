package org.live.im.router.interfaces.rpc;

import org.live.im.dto.ImMsgBody;

public interface ImRouterRpc {

    /**
     * 按用户id进行消息发送
     * @param objectId
     * @param imMsgBody
     */
    boolean sendMsg(Long objectId, ImMsgBody imMsgBody);
}
