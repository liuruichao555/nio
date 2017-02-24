package netty.priva;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import netty.priva.codec.NettyMessageDecoder;
import netty.priva.codec.NettyMessageEncoder;
import netty.priva.server.HeartBeatRespHandler;
import netty.priva.server.LoginAuthRespHandler;

/**
 * 服务端
 *
 * @author liuruichao
 * Created on 2015-12-11 16:06
 */
public class NettyServer {
    private int port = 9999;

    public void bind() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new NettyMessageDecoder(1024 * 1024, 4, 4),
                                    new NettyMessageEncoder(),
                                    new ReadTimeoutHandler(50),
                                    new LoginAuthRespHandler(),
                                    new HeartBeatRespHandler());
                        }
                    });
            ChannelFuture f = b.bind("127.0.0.1", 9999).sync();
            System.out.println("Netty server start ok : 127.0.0.1:9999");
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        NettyServer server = new NettyServer();
        server.bind();
    }
}
