package org.live.im.interfaces;

public enum ImMsgCodeEnum {
    IMS_LOGIN_MSG(1001,"登录im消息包"),
    IMS_LOGOUT_MSG(1002,"登出im消息包"),
    IMS_BIZ_MSG(1003,"常规业务消息包"),
    IMS_HEARTBEAT_MSG(1004,"im服务器心跳消息包");
    private int code;
    private String desc;

    ImMsgCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }


    public String getDesc() {
        return desc;
    }

}
