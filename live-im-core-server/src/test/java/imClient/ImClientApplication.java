package imClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.live.im.core.server.common.ImMsg;
import org.live.im.core.server.common.ImMsgDecoder;
import org.live.im.core.server.common.ImMsgEncoder;
import org.live.im.interfaces.ImMsgCodeEnum;

public class ImClientApplication {

    private void startConnection(String address,int port) throws InterruptedException{
        EventLoopGroup clientGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                System.out.println("初始化连接建立");
                ch.pipeline().addLast(new ImMsgDecoder());
                ch.pipeline().addLast(new ImMsgEncoder());
                ch.pipeline().addLast(new ClientHandler());
            }
        });
        ChannelFuture channelFuture = bootstrap.connect(address,port).sync();
        Channel channel = channelFuture.channel();
        for (int i = 0; i < 100; i++) {
            channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IMS_LOGIN_MSG.getCode(), "login"));
            channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IMS_LOGOUT_MSG.getCode(), "logout"));
            channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IMS_BIZ_MSG.getCode(), "biz"));
            channel.writeAndFlush(ImMsg.build(ImMsgCodeEnum.IMS_HEARTBEAT_MSG.getCode(), "heart"));
            Thread.sleep(3000);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ImClientApplication client = new ImClientApplication();
        client.startConnection("localhost", 9090);
    }
}
