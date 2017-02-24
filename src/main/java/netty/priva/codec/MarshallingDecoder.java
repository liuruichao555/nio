package netty.priva.codec;

import io.netty.buffer.ByteBuf;
import org.apache.log4j.Logger;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

/**
 * MarshallingDecoder
 *
 * @author liuruichao
 * Created on 2015-12-11 13:30
 */
public class MarshallingDecoder {
    private final Logger logger = Logger.getLogger(MarshallingDecoder.class);
    private Unmarshaller unmarshaller;

    public MarshallingDecoder() throws IOException {
        unmarshaller = MarshallingCodecFactory.buildUnMarshalling();
    }

    protected Object decode(ByteBuf in) throws Exception {
        int objectSize = in.readInt();
        ByteBuf buf = in.slice(in.readerIndex(), objectSize);
        ByteInput input = new ChannelBufferByteInput(buf);
        try {
            unmarshaller.start(input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            in.readerIndex(in.readerIndex() + objectSize);
            return obj;
        } catch (Exception e) {
            logger.error("MarshallingDecoder.decode()", e);
        } finally {
            unmarshaller.close();
        }
        return null;
    }
}
