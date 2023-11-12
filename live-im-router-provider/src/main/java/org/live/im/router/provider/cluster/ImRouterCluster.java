package org.live.im.router.provider.cluster;

import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.Cluster;
import org.apache.dubbo.rpc.cluster.Directory;

/**
 * @author :Joseph Ho
 * Description: 基于cluster去做spi扩展，实现根据rpc上下文来选择具体请求的机器
 * Date: 17:05 2023/10/13
 */
public class ImRouterCluster implements Cluster {
    @Override
    public <T> Invoker<T> join(Directory<T> directory, boolean buildFilterChain) throws RpcException {

        return new ImRouterClusterInvoker<>(directory);
    }
}
