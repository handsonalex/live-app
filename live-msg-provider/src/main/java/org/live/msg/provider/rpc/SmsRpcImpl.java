package org.live.msg.provider.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import org.live.msg.dto.MsgCheckDTO;
import org.live.msg.enums.MsgSendResultEnum;
import org.live.msg.interfaces.ISmsRpc;

@DubboService
public class SmsRpcImpl implements ISmsRpc {
    @Override
    public MsgSendResultEnum sendLoginCode(String phone) {
        return null;
    }

    @Override
    public MsgCheckDTO checkLoginCode(String phone, Integer code) {
        return null;
    }

    @Override
    public void insertOne(String phone, Integer code) {

    }
}
