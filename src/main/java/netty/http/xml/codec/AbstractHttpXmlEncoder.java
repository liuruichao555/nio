package netty.http.xml.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;

import java.io.StringWriter;
import java.nio.charset.Charset;

/**
 * AbstractHttpXmlEncoder
 *
 * @author liuruichao
 * Created on 2015-12-09 14:20
 */
public abstract class AbstractHttpXmlEncoder<T> extends MessageToMessageEncoder<T> {
    private final Logger logger = Logger.getLogger(AbstractHttpXmlEncoder.class);
    protected final static String CHARSET_NAME = "UTF-8";
    protected final static Charset UTF_8 = Charset.forName(CHARSET_NAME);
    protected IBindingFactory factory = null;
    protected StringWriter writer = null;

    protected ByteBuf encode0(ChannelHandlerContext ctx, Object body) throws Exception {
        factory = BindingDirectory.getFactory(body.getClass());
        writer = new StringWriter();
        IMarshallingContext mctx = factory.createMarshallingContext();
        mctx.setIndent(2);
        mctx.marshalDocument(body, CHARSET_NAME, null, writer);
        String xmlStr = writer.toString();
        writer.close();
        writer = null;
        ByteBuf encodeBuf = Unpooled.copiedBuffer(xmlStr, UTF_8);
        return encodeBuf;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (writer != null) {
            writer.close();
            writer = null;
        }
    }
}
