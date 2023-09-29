package org.live.im.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.live.im.core.server.common.ImMsgDecoder;
import org.live.im.core.server.common.ImMsgEncoder;
import org.live.im.core.server.handler.ImServerCoreHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author :Joseph Ho
 * Description: netty启动类
 * Date: 22:09 2023/9/24
 */
public class NettyImServerApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyImServerApplication.class);

    //指定监听的端口
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    //基于netty去启动一个java进程，绑定监听的端口
    public void startApplication(int port) throws InterruptedException {
        setPort(port);
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
                LOGGER.info("初始化连接渠道");
                //设计消息体
                //增加编解码器
                channel.pipeline().addLast(new ImMsgDecoder());
                channel.pipeline().addLast(new ImMsgEncoder());
                channel.pipeline().addLast(new ImServerCoreHandler());
                //设置netty处理handler
            }
        });
        //基于JVM的钩子函数去实现优雅关机
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        LOGGER.info("服务启动成功，监听端口为：" + getPort());
        //这里会阻塞掉主线程，实现服务长期开启
        channelFuture.channel().closeFuture().sync();

    }

    public static void main(String[] args) throws InterruptedException {
        NettyImServerApplication nettyImServerApplication = new NettyImServerApplication();
        nettyImServerApplication.startApplication(9090);
    }

}
