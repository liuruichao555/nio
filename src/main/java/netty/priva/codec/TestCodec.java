package netty.priva.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import netty.priva.model.Header;
import netty.priva.model.NettyMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试编解码
 *
 * @author liuruichao
 *         Created on 2015-12-11 17:46
 */
public class TestCodec {
    MarshallingEncoder marshallingEncoder;
    MarshallingDecoder marshallingDecoder;

    public TestCodec() throws IOException {
        marshallingDecoder = new MarshallingDecoder();
        marshallingEncoder = new MarshallingEncoder();
    }

    public NettyMessage getMessage() {
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setLength(123);
        header.setSessionId(99999);
        header.setType((byte) 1);
        header.setPriority((byte) 7);
        Map<String, Object> attachment = new HashMap<String, Object>();
        for (int i = 0; i < 10; i++) {
            attachment.put("ciyt --> " + i, "lilinfeng " + i);
        }
        header.setAttachment(attachment);
        nettyMessage.setHeader(header);
        nettyMessage.setBody("abcdefg-----------------------AAAAAA");
        return nettyMessage;
    }

    public ByteBuf encode(NettyMessage msg) throws Exception {
        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeInt((msg.getHeader().getCrcCode()));
        sendBuf.writeInt((msg.getHeader().getLength()));
        sendBuf.writeLong((msg.getHeader().getSessionId()));
        sendBuf.writeByte((msg.getHeader().getType()));
        sendBuf.writeByte((msg.getHeader().getPriority()));
        sendBuf.writeInt((msg.getHeader().getAttachment().size()));
        byte[] keyArray = null;
        Object value = null;

        for (String key : msg.getHeader().getAttachment().keySet()) {
            keyArray = key.getBytes("UTF-8");
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            value = msg.getHeader().getAttachment().get(key);
            marshallingEncoder.encode(value, sendBuf);
        }
        keyArray = null;
        value = null;
        if (msg.getBody() != null) {
            marshallingEncoder.encode(msg.getBody(), sendBuf);
        } else
            sendBuf.writeInt(0);
        sendBuf.setInt(4, sendBuf.readableBytes());
        return sendBuf;
    }

    public NettyMessage decode(ByteBuf in) throws Exception {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(in.readInt());
        header.setLength(in.readInt());
        header.setSessionId(in.readLong());
        header.setType(in.readByte());
        header.setPriority(in.readByte());

        int size = in.readInt();
        if (size > 0) {
            Map<String, Object> attch = new HashMap<String, Object>(size);
            int keySize = 0;
            byte[] keyArray = null;
            String key = null;
            for (int i = 0; i < size; i++) {
                keySize = in.readInt();
                keyArray = new byte[keySize];
                in.readBytes(keyArray);
                key = new String(keyArray, "UTF-8");
                attch.put(key, marshallingDecoder.decode(in));
            }
            keyArray = null;
            key = null;
            header.setAttachment(attch);
        }
        if (in.readableBytes() > 4) {
            message.setBody(marshallingDecoder.decode(in));
        }
        message.setHeader(header);
        return message;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        TestCodec testC = new TestCodec();
        NettyMessage message = testC.getMessage();
        System.out.println(message + "[body ] " + message.getBody());

        for (int i = 0; i < 5; i++) {
            ByteBuf buf = testC.encode(message);
            NettyMessage decodeMsg = testC.decode(buf);
            System.out.println(decodeMsg + "[body ] " + decodeMsg.getBody());
            System.out
                    .println("-------------------------------------------------");

        }
    }
}
