package org.live.id.generate.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import org.live.id.generate.interfaces.IdGenerateRpc;

@DubboService
public class IdGenerateRpcImpl implements IdGenerateRpc {
    @Override
    public Long getSeqId(Integer id) {
        return null;
    }

    @Override
    public Long getUnSeqId(Integer id) {
        return null;
    }
}
