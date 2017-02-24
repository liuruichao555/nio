package netty.priva.codec;

import io.netty.buffer.ByteBuf;
import org.apache.log4j.Logger;
import org.jboss.marshalling.Marshaller;

import java.io.IOException;

/**
 * Netty消息编码工具类
 *
 * @author liuruichao
 * Created on 2015-12-11 13:14
 */
public class MarshallingEncoder {
    private final Logger logger = Logger.getLogger(MarshallingEncoder.class);
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];
    private Marshaller marshaller;

    public MarshallingEncoder() throws IOException {
        marshaller = MarshallingCodecFactory.buildMarshalling();
    }

    protected void encode(Object msg, ByteBuf out) throws Exception {
        System.out.println("MarshallingEncoder");
        try {
            int lengthPos = out.writerIndex();
            out.writeBytes(LENGTH_PLACEHOLDER);
            ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
            marshaller.start(output);
            marshaller.writeObject(msg);
            marshaller.finish();
            out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
        } catch (Exception e) {
            logger.error("MarshallingEncoder.encode()", e);
        } finally {
            marshaller.close();
        }
    }
}
