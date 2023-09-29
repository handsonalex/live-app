package org.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.live.im.core.server.common.ImMsg;
import org.live.im.core.server.handler.ImHandlerFactory;
import org.live.im.core.server.handler.SimplyHandler;
import org.live.im.interfaces.ImMsgCodeEnum;

import java.util.HashMap;
import java.util.Map;

public class ImHandlerFactoryImpl implements ImHandlerFactory {

    private static Map<Integer, SimplyHandler> simplyHandlerMap = new HashMap<>();
    static {
        //登录消息包、登录token认证，channel和userId关联
        //登出消息包，正常断开im连接的时候发送
        //业务消息包，最常用的消息类型，列入我们的im发送数据，或者接收数据的时候会用到
        //心跳消息包，定时会给im发送，汇报功能
        simplyHandlerMap.put(ImMsgCodeEnum.IMS_LOGIN_MSG.getCode(), new LoginMsgHandler());
        simplyHandlerMap.put(ImMsgCodeEnum.IMS_LOGOUT_MSG.getCode(), new LogoutMsgHandler());
        simplyHandlerMap.put(ImMsgCodeEnum.IMS_BIZ_MSG.getCode(), new BizImMsgHandler());
        simplyHandlerMap.put(ImMsgCodeEnum.IMS_HEARTBEAT_MSG.getCode(), new HeartBeatMsgHandler());
    }
    @Override
    public void doMsgHandler(ChannelHandlerContext channelHandlerContext, ImMsg imMsg) {
        SimplyHandler simplyHandler = simplyHandlerMap.get(imMsg.getCode());
        if (simplyHandler == null){
            throw new IllegalArgumentException("msg code is error,code is: " + imMsg.getCode());
        }
        simplyHandler.handler(channelHandlerContext, imMsg);
    }
}
