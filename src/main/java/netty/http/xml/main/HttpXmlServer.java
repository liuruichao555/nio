package netty.http.xml.main;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import netty.http.xml.codec.HttpXmlRequest;
import netty.http.xml.codec.HttpXmlRequestDecoder;
import netty.http.xml.codec.HttpXmlResponse;
import netty.http.xml.codec.HttpXmlResponseDecoder;
import netty.http.xml.codec.HttpXmlResponseEncoder;
import netty.http.xml.model.Address;
import netty.http.xml.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * HttpXmlServer
 *
 * @author liuruichao
 *         Created on 2015-12-10 10:36
 */
public class HttpXmlServer {
    private int port = 9999;

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("xml-decoder", new HttpXmlRequestDecoder(Order.class, true));
                            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            ch.pipeline().addLast("xml-encoder", new HttpXmlResponseEncoder());
                            ch.pipeline().addLast("xmlServerHandler", new HttpXmlServerHandler());
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

    private class HttpXmlServerHandler extends SimpleChannelInboundHandler<HttpXmlRequest> {
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpXmlRequest msg) throws Exception {
            HttpRequest request = msg.getRequest();
            Order order = (Order) msg.getBody();
            System.out.println("Http server receive request : " + order);
            dobusiness(order);
            ChannelFuture f = ctx.writeAndFlush(new HttpXmlResponse(null, order));
            if (!isKeepAlive(request)) {
                f.addListener(future -> ctx.close());
            }
        }

        private boolean isKeepAlive(HttpRequest request) {
            return "keep-alive".equals(request.headers().get("Connection"));
        }

        private void dobusiness(Order order) {
            order.getCustomer().setFirstName("狄");
            order.getCustomer().setLastName("仁杰");
            //List<String> midNames = new ArrayList<>();
            //midNames.add("李元芳");
            //order.getCustomer().setMiddleNames(midNames);
            Address address = order.getBillTo();
            address.setCity("洛阳");
            address.setCountry("大唐");
            address.setState("河南道");
            address.setPostCode("471000");
            order.setBillTo(address);
            order.setShipTo(address);
        }
    }

    public static void main(String[] args) {
        HttpXmlServer server = new HttpXmlServer();
        server.run();
    }
}
