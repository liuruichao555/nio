package app.client;

import app.codec.MessageDecoder;
import app.codec.MessageEncoder;
import app.config.AppConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.log4j.Logger;

/**
 * 客户端入口
 * 
 * @author liuruichao
 * Created on 2015-12-22 16:19
 */
public class ClientMain {
    private Logger logger = Logger.getLogger(ClientMain.class);

    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new MessageDecoder(1024 * 1024, 0, 4),
                                    new MessageEncoder(),
                                    new ReadTimeoutHandler(50),
                                    new LoginAuthReqHandler()
                            );
                        }
                    });
            ChannelFuture f = b.connect(AppConfig.SERVER_HOST, AppConfig.SERVER_PORT).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("ClientMain.connect()", e);
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        ClientMain main = new ClientMain();
        main.connect();
    }
}
