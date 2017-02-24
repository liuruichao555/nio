package netty.demo2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * TimeServer
 *
 * @author liuruichao
 * @date 15/12/7 上午9:54
 */
public class TimeServer {
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
                            ch.pipeline().addLast(new TimeEncoder()).addLast(new TimeServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
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
            ChannelFuture f = ctx.writeAndFlush(new UnixTime());
            f.addListener(ChannelFutureListener.CLOSE);

            /*final ByteBuf time = ctx.alloc().buffer(4);
            time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

            final ChannelFuture f = ctx.writeAndFlush(time);
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    assert f == future;
                    ctx.close();
                }
            });*/
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    private class TimeEncoder extends MessageToByteEncoder<UnixTime> {
        @Override
        protected void encode(ChannelHandlerContext ctx, UnixTime msg, ByteBuf out) throws Exception {
            out.writeInt((int) msg.value());
        }
    }

    public static void main(String[] args) {
        TimeServer server = new TimeServer();
        server.run();
    }
}
