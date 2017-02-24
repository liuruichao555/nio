package netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import netty.codec.protobuf.SubjscribeReqProto;
import netty.codec.protobuf.SubjscribeRespProto;

/**
 * protobuf 进行编解码
 *
 * @author liuruichao
 *         Created on 2015-12-08 10:30
 */
public class SubReqServer {
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
                            ch.pipeline().addLast(
                                    new ProtobufVarint32FrameDecoder(),
                                    new ProtobufDecoder(SubjscribeReqProto.SubscribeReq.getDefaultInstance()),
                                    new ProtobufVarint32LengthFieldPrepender(),
                                    new ProtobufEncoder(),
                                    new SubReqServerHandler());
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

    private class SubReqServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            SubjscribeReqProto.SubscribeReq req = (SubjscribeReqProto.SubscribeReq) msg;
            if ("liuruichao".equals(req.getUserName())) {
                System.out.println("Service accept client subscribe req : [ " + req.toString() + " ]");
                ctx.writeAndFlush(resp(req.getSubReqID()));
            }
        }

        private SubjscribeRespProto.SubscribeResp resp(int subReqID) {
            SubjscribeRespProto.SubscribeResp.Builder builder = SubjscribeRespProto.SubscribeResp.newBuilder();
            builder.setSubReqID(subReqID);
            builder.setRespCode(0);
            builder.setDesc("Netty book order succeed, 3 days later, sent to the designated address");
            return builder.build();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    public static void main(String[] args) {
        SubReqServer server = new SubReqServer();
        server.run();
    }
}
