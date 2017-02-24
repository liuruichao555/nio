package netty.demo3;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;

/**
 * Client
 *
 * @author liuruichao
 * Created on 2015-12-16 11:04
 */
public class Client {
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
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public void sendMsg(ChannelHandlerContext ctx, String msg) throws UnsupportedEncodingException {
        ByteBuf buf = Unpooled.copiedBuffer(msg.getBytes("utf-8"));
        ctx.writeAndFlush(buf);
    }

    private class ClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            new Thread(() -> {
                Scanner in = new Scanner(System.in);
                while (true) {
                    System.out.print("请输入发送的内容:");
                    String req = in.next();
                    try {
                        sendMsg(ctx, req);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if ("bye".equals(req)) {
                        ctx.close();
                        break;
                    }
                }
            }).start();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            byte[] resp = new byte[buf.readableBytes()];
            buf.readBytes(resp);
            String respBody = new String(resp);
            System.out.println("response body : " + respBody);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    public static void main(String[] args) {
        final Client client = new Client();
        client.connect();
    }
}
