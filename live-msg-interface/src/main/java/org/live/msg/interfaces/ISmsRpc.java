package org.live.msg.interfaces;

import org.live.msg.dto.MsgCheckDTO;
import org.live.msg.enums.MsgSendResultEnum;

public interface ISmsRpc {

    /**
     * 发送短信接口
     *
     * @param phone
     * @return
     */
    MsgSendResultEnum sendLoginCode(String phone);
    /**
     * 校验登录验证码
     *
     * @param phone
     * @param code
     * @return
     */
    MsgCheckDTO checkLoginCode(String phone, Integer code);
    /**
     * 插入一条短信记录
     www.imooc.com
     *
     * @param phone
     * @param code
     */
    void insertOne(String phone, Integer code);


}
