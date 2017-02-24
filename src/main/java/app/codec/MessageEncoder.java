package app.codec;

import app.config.AppConfig;
import app.model.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * MessageEncoder
 * 
 * @author liuruichao
 * Created on 2015-12-23 10:51
 */
public class MessageEncoder extends MessageToByteEncoder<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        if (msg == null || msg.getHeader() == null) {
            throw new IllegalArgumentException("encoder message is null");
        }

        out.writeInt(msg.getHeader().getLength());
        out.writeInt(msg.getHeader().getCrcCode());
        // sessionId 长度
        byte[] buffer = msg.getHeader().getSessionId().getBytes(AppConfig.DEFAULT_CHARSET);
        int sidLen = buffer.length;
        out.writeInt(sidLen);
        out.writeBytes(buffer);
        out.writeInt(msg.getHeader().getType());
        out.writeInt(msg.getHeader().getPriority());
        if (msg.getBody() != null) {
            out.writeBytes(msg.getBody().getBytes(AppConfig.DEFAULT_CHARSET));
        } else {
            out.writeBytes("".getBytes(AppConfig.DEFAULT_CHARSET));
        }
        out.setInt(0, out.readableBytes() - 4);
    }
}
