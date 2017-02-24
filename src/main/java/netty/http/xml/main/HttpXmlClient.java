package netty.http.xml.main;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import netty.http.xml.codec.HttpXmlRequest;
import netty.http.xml.codec.HttpXmlResponse;
import netty.http.xml.factory.OrderFactory;
import netty.http.xml.codec.HttpXmlRequestEncoder;
import netty.http.xml.codec.HttpXmlResponseDecoder;
import netty.http.xml.model.Order;

/**
 * HttpXmlClient
 *
 * @author liuruichao
 * Created on 2015-12-10 10:24
 */
public class HttpXmlClient {
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
                            ch.pipeline().addLast("http-decoder", new HttpResponseDecoder());
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            // xml 解码器
                            ch.pipeline().addLast("xml-decoder", new HttpXmlResponseDecoder(Order.class, true));
                            ch.pipeline().addLast("http-encoder", new HttpRequestEncoder());
                            ch.pipeline().addLast("xml-encoder", new HttpXmlRequestEncoder());
                            ch.pipeline().addLast("xmlClientHandler", new HttpXmlClientHandler());
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

    private class HttpXmlClientHandler extends SimpleChannelInboundHandler<HttpXmlResponse> {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            HttpXmlRequest request = new HttpXmlRequest(null, OrderFactory.create(123));
            ctx.writeAndFlush(request);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpXmlResponse msg) throws Exception {
            System.out.println("The client receive response of http header is : " + msg.getHttpResponse().headers().names());
            System.out.println("The client receive response of http body is : " + msg.getResult());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    public static void main(String[] args) {
        HttpXmlClient client = new HttpXmlClient();
        client.connect();
    }
}
