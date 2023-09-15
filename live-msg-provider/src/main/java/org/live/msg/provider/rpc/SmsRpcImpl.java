package org.live.msg.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.live.msg.dto.MsgCheckDTO;
import org.live.msg.enums.MsgSendResultEnum;
import org.live.msg.interfaces.ISmsRpc;
import org.live.msg.provider.service.ISmsService;

@DubboService
public class SmsRpcImpl implements ISmsRpc {

    @Resource
    private ISmsService smsService;
    @Override
    public MsgSendResultEnum sendLoginCode(String phone) {
        return smsService.sendLoginCode(phone);
    }

    @Override
    public MsgCheckDTO checkLoginCode(String phone, Integer code) {
        return smsService.checkLoginCode(phone, code);
    }

    @Override
    public void insertOne(String phone, Integer code) {
        smsService.insertOne(phone, code);
    }
}
