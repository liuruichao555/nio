package netty.client;

import io.netty.bootstrap.Bootstrap;
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
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 粘包问题
 *
 * @author liuruichao
 * Created on 2015-12-07 16:42
 */
public class TimeClient2 {
    private String host = "127.0.0.1";
    private int port = 9999;

    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024))
                                         .addLast(new StringDecoder())
                                         .addLast(new TimeClient2Handler());
                        }
                    }).option(ChannelOption.TCP_NODELAY, true);
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    private class TimeClient2Handler extends ChannelInboundHandlerAdapter {
        private int counter = 0;
        private byte[] req;

        public TimeClient2Handler() {
            req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ByteBuf msg = null;
            for (int i = 0; i < 100; i++) {
                msg = Unpooled.buffer(req.length);
                msg.writeBytes(req);
                ctx.writeAndFlush(msg);
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            /*ByteBuf buf = (ByteBuf) msg;
            byte[] resp = new byte[buf.readableBytes()];
            buf.readBytes(resp);
            String body = new String(resp, "utf-8");*/
            String body = (String) msg;
            System.out.println("Now is : " + body + " ; the counter is : " + (++counter));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    public static void main(String[] args) {
        TimeClient2 client = new TimeClient2();
        client.connect();
    }
}
