package netty.priva;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import netty.priva.client.HeartBeatReqHandler;
import netty.priva.client.LoginAuthReqHandler;
import netty.priva.codec.NettyMessageDecoder;
import netty.priva.codec.NettyMessageEncoder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 客户端程序
 *
 * @author liuruichao
 * Created on 2015-12-11 15:39
 */
public class NettyClient {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private EventLoopGroup group = new NioEventLoopGroup();

    public void connect(int port, String host) {
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new NettyMessageDecoder(1024 * 1024, 4, 4),
                                    new NettyMessageEncoder(),
                                    new ReadTimeoutHandler(50),
                                    new LoginAuthReqHandler(),
                                    new HeartBeatReqHandler());
                        }
                    });
            // 发起异步连接操作
            ChannelFuture future = b.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 所有资源释放完成后,清空资源,再次发起重连操作
            executor.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    // 发起重连操作
                    connect(NettyConstant.PORT, NettyConstant.REMOTEIP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void main(String[] args) {
        NettyClient client = new NettyClient();
        client.connect(NettyConstant.PORT, NettyConstant.REMOTEIP);
    }
}
