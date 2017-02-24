package netty.http.xml.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.List;

/**
 * 请求消息解码
 *
 * @author liuruichao
 * Created on 2015-12-09 16:32
 */
public class HttpXmlRequestDecoder extends AbstractHttpXmlDecoder<FullHttpRequest> {
    public HttpXmlRequestDecoder(Class<?> clazz) {
        this(clazz, false);
    }

    public HttpXmlRequestDecoder(Class<?> clazz, boolean isPrint) {
        super(clazz, isPrint);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest msg, List<Object> out) throws Exception {
        if (!msg.getDecoderResult().isSuccess()) {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        HttpXmlRequest request = new HttpXmlRequest(msg, decode0(ctx, msg.content()));
        out.add(request);
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        response.headers().set("Content-Type", "text/html; charset=utf-8");
        ByteBuf buffer = Unpooled.copiedBuffer(("<body>Failure: " + status.toString() + "</body>").getBytes());
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
