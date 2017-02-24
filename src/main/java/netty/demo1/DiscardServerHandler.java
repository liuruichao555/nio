package netty.demo1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * DiscardServerHandler
 *
 * @author liuruichao
 * @date 15/12/4 下午4:02
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        ByteBuf result = Unpooled.wrappedBuffer("Hello Client!".getBytes());
        try {
            System.out.println("client " + ctx.channel() + " .");
            while (in.isReadable()) {
                System.out.print((char) in.readByte());
                System.out.flush();
            }
            System.out.println();
            ctx.writeAndFlush(result);
        } finally {
            // ByteBuf 引用计数减小1并释放对象
            ReferenceCountUtil.release(msg);
            // 服务器关闭与当前客户端的链接,客户端关闭
            ctx.close();
            System.out.println("client " + ctx.channel() + " closed .");
        }
    }

    @Override

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        cause.printStackTrace();
        ctx.close();
    }
}
