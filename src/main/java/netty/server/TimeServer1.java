package netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * TimeServer1
 * 
 * @author liuruichao
 * Created on 2015-12-07 13:20
 */
public class TimeServer1 {
    private int port = 9999;

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeServerHandler());
                }
            }).option(ChannelOption.SO_BACKLOG, 1024).childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(port).sync();
            //f.channel().close().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //workerGroup.shutdownGracefully();
            //bossGroup.shutdownGracefully();
        }
    }

    private class TimeServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("客户端连接事件.");
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            String body = new String(req);
            System.out.println(body);
            if ("query time".equals(body)) {
                byte[] response = (System.currentTimeMillis() + "").toString().getBytes();
                buf = Unpooled.buffer(response.length);
                buf.writeBytes(response);
                ctx.writeAndFlush(buf);
            }
        }
    }

    public static void main(String[] args) {
        TimeServer1 server = new TimeServer1();
        server.run();
    }
}
