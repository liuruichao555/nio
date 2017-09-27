package netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * WebSocketServer
 *
 * @author liuruichao
 * Created on 2015-12-10 13:30
 */
public class WebSocketServer {
    private int port = 9999;

    private String host = "192.168.1.132";

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
                            ch.pipeline().addLast("http-codec", new HttpServerCodec());
                            ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            ch.pipeline().addLast("handler", new WebSocketServerHandler());
                        }
                    });
            Channel channel = b.bind(port).sync().channel();
            System.out.println("Web socket server started at port " + port + ".");
            System.out.println("Open your browser and navigate to http://" + host + ":" + port + "/");
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
        private final Logger logger = Logger.getLogger(WebSocketServerHandler.class);
        private WebSocketServerHandshaker handshaker;

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            // 传统的HTTP 接入
            if (msg instanceof FullHttpRequest) {
                handleHttpRequest(ctx, (FullHttpRequest) msg);
            } else if (msg instanceof WebSocketFrame) {
                // WebSocket 接入
                handleWebSocketFrame(ctx, (WebSocketFrame) msg);
            }
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

        private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
            // 如果http解码失败,返回HTTP异常
            if (!req.getDecoderResult().isSuccess()
                    || (!"websocket".equals(req.headers().get("Upgrade")))) {
                sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
                return;
            }
            // 构造握手响应返回, 本机测试
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(String.format("ws://%s:%s/websocket", host, port), null, false);
            handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
            } else {
                handshaker.handshake(ctx.channel(), req);
            }

        }

        private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
            // 判断是否是关闭链路的命令
            if (frame instanceof CloseWebSocketFrame) {
                handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
                return;
            }
            // 判断是否是Ping消息
            if (frame instanceof PingWebSocketFrame) {
                ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
                return;
            }
            // 仅支持文本消息,不支持二进制消息
            if (!(frame instanceof TextWebSocketFrame)) {
                throw new UnsupportedOperationException("只支持文本消息.");
            }
            // 返回应答消息
            String request = ((TextWebSocketFrame) frame).text();
            logger.debug(String.format("%s received %s", ctx.channel(), request));
            ctx.channel().write(new TextWebSocketFrame(request + " , 欢迎使用Netty WebSocket 服务, 现在时刻:" + new Date().toString()));
        }

        private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
            // 返回应答给客户端
            if (response.getStatus().code() != 200) {
                ByteBuf buf = Unpooled.copiedBuffer(response.getStatus().toString(), CharsetUtil.UTF_8);
                response.content().writeBytes(buf);
                buf.release();
                HttpHeaders.setContentLength(response, response.content().readableBytes());
            }

            // 如果是非Keep-Alive, 关闭连接
            ChannelFuture f = ctx.channel().writeAndFlush(response);
            if (response.getStatus().code() != 200 || !isKeepAlive(request)) {
                f.addListener(ChannelFutureListener.CLOSE);
            }
        }

        private boolean isKeepAlive(FullHttpRequest request) {
            return "keep-alive".equals(request.headers().get("Connection"));
        }
    }

    public static void main(String[] args) {
        WebSocketServer server = new WebSocketServer();
        server.run();
    }
}
