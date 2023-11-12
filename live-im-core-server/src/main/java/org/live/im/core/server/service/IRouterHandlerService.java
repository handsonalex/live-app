package org.live.im.core.server.service;

import org.live.im.dto.ImMsgBody;

public interface IRouterHandlerService {
    /**
     * 当收到业务服务的请求，进行处理
     *
     * @param imMsgBody
     */
    void onReceive(ImMsgBody imMsgBody);
}
