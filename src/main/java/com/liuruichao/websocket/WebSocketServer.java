package com.liuruichao.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.log4j.Logger;

/**
 * 在线聊天
 *
 * @author liuruichao
 * Created on 2015-12-16 14:54
 */
public class WebSocketServer {
    private final Logger logger = Logger.getLogger(WebSocketServer.class);

    public void run(String host, int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new HttpServerCodec(),
                                    new HttpObjectAggregator(65536),
                                    new ChunkedWriteHandler(),
                                    new WebSocketServerHandler(host, port));
                        }
                    });
            ChannelFuture f = b.bind(host, port).sync();
            logger.info("Web socket server started : http://" + host + ":" + port + " /");
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("WebSocketServer.run()", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 9999;
        WebSocketServer server = new WebSocketServer();
        server.run(host, port);
    }
}
