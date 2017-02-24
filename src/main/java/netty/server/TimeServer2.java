package netty.server;

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
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.Date;

/**
 * 粘包问题
 *
 * @author liuruichao
 * Created on 2015-12-07 16:39
 */
public class TimeServer2 {
    private int port = 9999;

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024))
                                         .addLast(new StringDecoder())
                                         .addLast(new TimeServer2Handler());
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            //f.channel().close().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //workerGroup.shutdownGracefully();
            //bossGroup.shutdownGracefully();
        }
    }

    private class TimeServer2Handler extends ChannelInboundHandlerAdapter {
        private int counter = 0;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            /*ByteBuf buf = (ByteBuf) msg;
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            // 去除最后一个分隔符
            String body = new String(req, "utf-8").substring(0, req.length - System.getProperty("line.separator").length());
            */
            String body = (String) msg;
            System.out.println("The time server receive order : " + body + " ; the counter is : " + (++counter));
            String currentTime = "QUERYR TIME ORDER".equalsIgnoreCase(body) ? new Date().toString() : "BAD ORDER";
            currentTime = currentTime + System.getProperty("line.separator");
            ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
            ctx.writeAndFlush(resp);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    public static void main(String[] args) {
        TimeServer2 server = new TimeServer2();
        server.run();
    }
}
