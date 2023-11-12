package org.live.im.core.server.starter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.live.im.core.server.common.ChannelHandlerContextCache;
import org.live.im.core.server.common.ImMsgDecoder;
import org.live.im.core.server.common.ImMsgEncoder;
import org.live.im.core.server.handler.ImServerCoreHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
public class NettyImServerStarter implements InitializingBean {

    @Value("${app.im.port}")
    private int port;

    @Resource
    private ImServerCoreHandler imServerCoreHandler;

    @Resource
    private Environment environment;

    //基于netty去启动一个java进程，绑定监听的端口
    public void startApplication() throws InterruptedException {
        //处理accept事件
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //处理read write事件
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup,workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        //netty初始化相关的handler
        bootstrap.childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                log.info("初始化连接渠道");
                //设计消息体
                //增加编解码器
                channel.pipeline().addLast(new ImMsgDecoder());
                channel.pipeline().addLast(new ImMsgEncoder());
                //设置netty处理handler
                channel.pipeline().addLast(imServerCoreHandler);
            }
        });
        //基于JVM的钩子函数去实现优雅关机
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));
        //获取im服务的注册ip
        String registryIp = environment.getProperty("DUBBO_IP_TO_REGISTRY");
        String registryPort = environment.getProperty("DUBBO_PORT_TO_REGISTRY");
        if (StringUtils.isEmpty(registryIp) || StringUtils.isEmpty(registryPort)){
            throw new IllegalArgumentException("启动参数中注册端口和注册ip不能为空");
        }
        ChannelHandlerContextCache.setServerIpAddress(registryIp + ":" + registryPort);
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        log.info("服务启动成功，监听端口为：" + port);
        //这里会阻塞掉主线程，实现服务长期开启
        channelFuture.channel().closeFuture().sync();

    }
    @Override
    public void afterPropertiesSet() throws Exception {
        Thread nettyServerThread = new Thread(() -> {
            try {
                startApplication();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        nettyServerThread.setName("live-im-server");
        nettyServerThread.start();
    }
}
