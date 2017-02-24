package netty.demo3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Date;

/**
 * Server
 *
 * @author liuruichao
 * Created on 2015-12-16 11:01
 */
public class Server {
    private int port = 9999;

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private class ServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            String reqBody = new String(req);
            System.out.println("request body : " + reqBody);
            String respBody = null;
            switch (reqBody) {
                case "bye":
                    ctx.close();
                    break;
                case "time":
                    respBody = new Date().toString();
                    buf = Unpooled.copiedBuffer(respBody.getBytes("utf-8"));
                    ctx.writeAndFlush(buf);
                    break;
                default:
                    respBody = "我不知道你说的是什么...";
                    buf = Unpooled.copiedBuffer(respBody.getBytes("utf-8"));
                    ctx.writeAndFlush(buf);
                    break;
            }
            //System.out.println("current socket num : " + )
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
