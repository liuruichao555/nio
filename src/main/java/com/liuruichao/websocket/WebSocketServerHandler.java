package com.liuruichao.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 *
 * @author liuruichao
 * Created on 2015-12-16 15:12
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {
    private final Logger logger = Logger.getLogger(WebSocketServerHandler.class);
    private WebSocketServerHandshaker handshaker;
    private final static Set<ChannelHandlerContext> ctxList = new HashSet<>();
    private final static ExecutorService exec = Executors.newFixedThreadPool(2);
    private final String host;
    private final int port;

    public WebSocketServerHandler(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("WebSocketServerHandler error : ", cause);
        ctx.close();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // 如果http解码失败,返回HTTP异常
        if (!req.getDecoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        String type = getImType(req.getUri());
        if (StringUtils.isEmpty(type)) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        logger.debug("IM type : " + type);

        // 构造握手响应返回, 本机测试
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://" + host + ":" + port + "/websocket", null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }

        // 聊天室
        ctxList.add(ctx);
        logger.debug("ctxList size : " + ctxList.size());
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
            throw new UnsupportedOperationException("只支持文本消息. ");
        }
        // 返回应答消息
        String request = ((TextWebSocketFrame) frame).text();
        logger.debug(String.format("%s received %s", ctx.channel(), request));
        int openCount = 0;
        Iterator<ChannelHandlerContext> iterator = ctxList.iterator();
        while (iterator.hasNext()) {
            ChannelHandlerContext context = iterator.next();
            if (context != ctx) {
                if (context.channel().isActive()) {
                    openCount ++;
                    context.writeAndFlush(new TextWebSocketFrame(request));
                } else {
                    // 删除无效链路
                    context.close();
                    iterator.remove();
                }
            }
        }
        logger.debug("openCount : " + openCount);
        logger.debug("remove context : " + ctxList.size());
        /*
        if (ctx1 == null || ctx1 == ctx) {
            ctx1 = ctx;
            ctx.channel().write(new TextWebSocketFrame("ctx1 send success"));
            if (ctx2 != null) {
                ctx2.writeAndFlush(new TextWebSocketFrame(request));
            }
        } else if (ctx2 == null || ctx2 == ctx) {
            ctx2 = ctx;
            ctx.channel().write(new TextWebSocketFrame("ctx2 send success"));
            if (ctx1 != null) {
                ctx1.writeAndFlush(new TextWebSocketFrame(request));
            }
        } else {
            ctx.channel().write(new TextWebSocketFrame("当前队列已满,请稍后再试!"));
        }
        */
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

    private String getImType(String uri) {
        String type = null;
        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        Map<String, List<String>> parameters = decoder.parameters();
        if (parameters != null) {
            List<String> typeList = parameters.get("type");
            if (typeList != null && typeList.size() > 0) {
                type = typeList.get(0);
            }
        }
        return type;
    }
}
