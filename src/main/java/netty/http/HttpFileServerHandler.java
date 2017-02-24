package netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

/**
 * HttpFileServerHandler
 * 
 * @author liuruichao
 * Created on 2015-12-08 14:19
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final Logger logger = Logger.getLogger(HttpFileServerHandler.class);
    private final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
    private final String url;

    public HttpFileServerHandler(String url) {
        this.url = url;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        if (!msg.getDecoderResult().isSuccess()) {
            logger.debug("!msg.getDecoderResult().isSuccess()");
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        if (msg.getMethod() != HttpMethod.GET) {
            logger.debug("type != GET");
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        final String uri = msg.getUri();
        final String path = sanitizeUri(uri);
        logger.debug("request path : " + path);
        if (path == null) {
            logger.debug("path == null");
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }
        File file = new File(path);
        if (file.isHidden() || !file.exists()) {
            logger.debug("file hidden or file not found");
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        if (!file.isFile()) {
            // 文件夹
            endListing(ctx, file);
            return;
        }
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            logger.error("randomAccessFile", e);
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        long fileLength = randomAccessFile.length();
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set("content-length", fileLength);
        //setContentTypeHeader(response, file);
        //response.headers().set("fileName", file.getName());
        logger.debug("request connection : " + msg.headers().get("Connection"));
        if (isKeepAlive(msg)) {
            response.headers().set("Connection", HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.write(response);
        logger.debug("fileLength : " + fileLength);

        ChannelFuture sendFileFuture =
                ctx.write(new ChunkedFile(randomAccessFile, 0, fileLength, 8192), ctx.newProgressivePromise());
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
                // TODO 下载时的进度
                //System.out.println("oprationProgressed");
            }

            @Override
            public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                logger.debug("发送完成.");
            }
        });
        sendFileFuture.addListener(ChannelFutureListener.CLOSE);
        sendFileFuture.channel().flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("HttpFileServerHandler.exceptionCaught()", cause);
        ctx.close();
    }

    private String sanitizeUri(String uri) {
        try {
            uri = URLDecoder.decode(uri, "utf-8");
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                throw new Error();
            }
        }
        uri = uri.replace('/', File.separatorChar);
        if (uri.contains(File.separator + ".")
                || uri.contains("." + File.separatorChar)
                || uri.startsWith(".")
                || uri.endsWith(".")
                /*|| !ALLOWED_FILE_NAME.matcher(uri).matches()*/) {
            return null;
        }
        logger.debug("uri : " + uri);
        logger.debug("separator : " + File.separator);
        logger.debug("separatorChar : " + File.separatorChar);
        return url + uri;
    }


    private void endListing(ChannelHandlerContext ctx, File dir) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set("Content-Type", "text/html; charset=utf-8");
        StringBuilder buf = new StringBuilder();
        buf.append("<body>");

        for (File f : dir.listFiles()) {
            if (!f.getName().startsWith(".")) {
                buf.append("<a href='" + f.getAbsolutePath().replace(url, "") + "'>" + f.getName() + "</a><br/>");
            }
        }

        buf.append("</body>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        response.headers().set("Content-Type", "text/html; charset=utf-8");
        ByteBuf buffer = Unpooled.copiedBuffer(("<body>Failure: " + status.toString() + "</body>").getBytes());
        response.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private boolean isKeepAlive(FullHttpRequest request) {
        return "keep-alive".equals(request.headers().get("Connection"));
    }
}