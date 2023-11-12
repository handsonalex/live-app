package org.live.im.core.server.common;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

public class ChannelHandlerContextCache {

    private static String SERVER_IP_ADDRESS = "";

    private static Map<Long, ChannelHandlerContext> channelHandlerContextMap = new HashMap<>();

    public static String getServerIpAddress() {
        return SERVER_IP_ADDRESS;
    }

    public static void setServerIpAddress(String serverIpAddress) {
        SERVER_IP_ADDRESS = serverIpAddress;
    }

    public static ChannelHandlerContext get(long userId){
        return channelHandlerContextMap.get(userId);
    }

    public static void put(long userId,ChannelHandlerContext channelHandlerContext){
        channelHandlerContextMap.put(userId,channelHandlerContext);
    }

    public static void remove(Long userId){
        channelHandlerContextMap.remove(userId);
    }
}
