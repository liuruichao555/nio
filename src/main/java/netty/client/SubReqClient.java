package netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import netty.codec.protobuf.SubjscribeReqProto;
import netty.codec.protobuf.SubjscribeRespProto;

/**
 * SubReqClient
 *
 * @author liuruichao
 *         Created on 2015-12-08 10:44
 */
public class SubReqClient {
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
                            ch.pipeline().addLast(
                                    new ProtobufVarint32FrameDecoder(),
                                    new ProtobufDecoder(SubjscribeRespProto.SubscribeResp.getDefaultInstance()),
                                    new ProtobufVarint32LengthFieldPrepender(),
                                    new ProtobufEncoder(),
                                    new SubReqClientHandler());
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

    private class SubReqClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            for (int i = 0; i < 10; i++) {
                ctx.write(subReq(i));
            }
            ctx.flush();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("Receive server response : [ " + msg + " ]");
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

        private SubjscribeReqProto.SubscribeReq subReq(int i) {
            SubjscribeReqProto.SubscribeReq.Builder builder = SubjscribeReqProto.SubscribeReq.newBuilder();
            builder.setSubReqID(i);
            builder.setUserName("liuruichao");
            builder.setProductName("netty");
            builder.setAddress("nicai");
            return builder.build();
        }
    }

    public static void main(String[] args) {
        SubReqClient client = new SubReqClient();
        client.connect();
    }
}
