package org.live.id.generate.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.live.id.generate.interfaces.IdGenerateRpc;
import org.live.id.generate.service.IdGenerateService;

@DubboService
public class IdGenerateRpcImpl implements IdGenerateRpc {
    @Resource
    private IdGenerateService idGenerateService;
    @Override
    public Long getSeqId(Integer id) {
        return idGenerateService.getSeqId(id);
    }

    @Override
    public Long getUnSeqId(Integer id) {
        return idGenerateService.getUnSeqId(id);
    }
}
