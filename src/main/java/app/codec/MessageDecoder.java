package app.codec;

import app.model.Header;
import app.model.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * MessageDecoder
 * 
 * @author liuruichao
 * Created on 2015-12-23 13:17
 */
public class MessageDecoder extends LengthFieldBasedFrameDecoder {
    public MessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        Message message = new Message();
        Header header = new Header();

        header.setLength(frame.readInt());
        header.setCrcCode(frame.readInt());
        // sessionId
        int sidLength = frame.readInt();
        byte[] buffer = new byte[sidLength];
        frame.readBytes(buffer);
        header.setSessionId(new String(buffer));
        header.setType(frame.readInt());
        header.setPriority(frame.readInt());

        message.setHeader(header);
        int bodyLen = frame.readableBytes();
        buffer = new byte[bodyLen];
        frame.readBytes(buffer);
        message.setBody(new String(buffer));
        return message;
    }
}
