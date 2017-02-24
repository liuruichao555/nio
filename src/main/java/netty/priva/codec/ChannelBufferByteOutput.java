package netty.priva.codec;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.ByteOutput;

import java.io.IOException;

/**
 * ChannelBufferByteOutput
 *
 * @author liuruichao
 * Created on 2015-12-11 13:24
 */
public class ChannelBufferByteOutput implements ByteOutput {
    private final ByteBuf buffer;

    public ChannelBufferByteOutput(ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int i) throws IOException {
        buffer.writeInt(i);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        buffer.writeBytes(bytes);
    }

    @Override
    public void write(byte[] bytes, int i, int i1) throws IOException {
        buffer.writeBytes(bytes, i, i1);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void flush() throws IOException {
    }

    public ByteBuf getBuffer() {
        return buffer;
    }
}
