package app.server;

import app.codec.MessageDecoder;
import app.codec.MessageEncoder;
import app.config.AppConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.log4j.Logger;

/**
 * 服务端入口
 * 
 * @author liuruichao
 * Created on 2015-12-22 16:19
 */
public class ServerMain {
    private final Logger logger = Logger.getLogger(ServerMain.class);

    public void bind() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, AppConfig.SERVER_BACK_LOG)
                    .handler(new LoggingHandler(AppConfig.SERVER_LOG_LEVEL))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new MessageDecoder(1024 * 1024, 0, 4),
                                    new MessageEncoder(),
                                    new ReadTimeoutHandler(50),
                                    new LoginAuthRespHandler()
                            );
                        }
                    });
            ChannelFuture f = b.bind(AppConfig.SERVER_HOST, AppConfig.SERVER_PORT).sync();
            logger.info(String.format("server start ok : %s:%d", AppConfig.SERVER_HOST, AppConfig.SERVER_PORT));
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("ServerMain.bind()", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        ServerMain main = new ServerMain();
        main.bind();
    }
}
