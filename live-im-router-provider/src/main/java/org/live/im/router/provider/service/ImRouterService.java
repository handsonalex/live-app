package org.live.im.router.provider.service;

import org.live.im.dto.ImMsgBody;

public interface ImRouterService {
    /**
     * 按用户id进行消息发送
     * @param objectId
     * @param imMsgBody
     */
    boolean sendMsg(Long objectId, ImMsgBody imMsgBody);
}
