package dubbo;

import org.apache.dubbo.config.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.live.user.interfaces.IUserRpc;
import org.live.user.provider.rpc.UserRpcImpl;

import java.util.HashMap;
import java.util.Map;

public class DubboTest {
    private static final String REGISTER_ADDRESS =
            "nacos://127.0.0.1:8848?namespace=qiyu-livetest&&username=qiyu&&password=qiyu";
    private static RegistryConfig registryConfig;
    private static ApplicationConfig applicationConfig;
    private static ReferenceConfig<IUserRpc>
            userRpcReferenceConfig;
    private static Map<Class, Object> referMap = new HashMap<>();
    private IUserRpc userRpc;

    @BeforeAll
    public static void initConfig() {
        registryConfig = new RegistryConfig();
        applicationConfig = new ApplicationConfig();
        registryConfig.setAddress(REGISTER_ADDRESS);
        applicationConfig.setName("dubbo-test-application");
        applicationConfig.setRegistry(registryConfig);
        userRpcReferenceConfig = new ReferenceConfig<>();
        //roundrobin random leastactive shortestresponse consistenthash
        userRpcReferenceConfig.setLoadbalance("random");
        userRpcReferenceConfig.setInterface(IUserRpc.class);
        referMap.put(IUserRpc.class,
                userRpcReferenceConfig.get());
    }

    //dubbo 初始化
    public void initProvider(){
        ProtocolConfig dubboProtocolConfig = new ProtocolConfig();
        dubboProtocolConfig.setPort(9090);
        dubboProtocolConfig.setName("dubbo");
        ServiceConfig<IUserRpc> serviceConfig = new ServiceConfig<>();
        serviceConfig.setInterface(IUserRpc.class);
        serviceConfig.setProtocol(dubboProtocolConfig);
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.setRef(new UserRpcImpl());
        //核心
        serviceConfig.export();
        System.out.println("服务暴露");
    }

    public void initConsumer(){
        ReferenceConfig<IUserRpc> userRpcReferenceConfig = new ReferenceConfig<>();
        userRpcReferenceConfig.setApplication(applicationConfig);
        userRpcReferenceConfig.setRegistry(registryConfig);
        userRpcReferenceConfig.setLoadbalance("random");
        userRpcReferenceConfig.setInterface(IUserRpc.class);
        userRpc = userRpcReferenceConfig.get();
    }

    @Test
    public void testUserRpc() {
        IUserRpc userRpc = (IUserRpc)
                referMap.get(IUserRpc.class);
//        for(int i=0;i<1000;i++) {
//            userRpc.test();
//        }
    }

//    public static void main(String[] args) throws InterruptedException {
//        initConfig();;
//        DubboTest dubboTest = new DubboTest();
//        dubboTest.initProvider();
//        dubboTest.initConsumer();
//        for (;;){
//            dubboTest.userRpc.test();
//            Thread.sleep(3000);
//        }
//    }
}
