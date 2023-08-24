package org.live.user.provider.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import org.live.user.interfaces.IUserRpc;
/**
 * @author :Joseph Ho
 * Description:
 * Date: 23:07 2023/8/22
 */
@DubboService(group = "test")
public class UserRpcImpl implements IUserRpc {
    @Override
    public String test() {
        System.out.println("this is dubbo test");
        return "success";
    }
}
