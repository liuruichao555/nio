package netty.priva.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.priva.MessageType;
import netty.priva.model.Header;
import netty.priva.model.NettyMessage;
import org.apache.log4j.Logger;

/**
 * 登录请求
 *
 * @author liuruichao
 * Created on 2015-12-11 13:50
 */
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = Logger.getLogger(LoginAuthReqHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLoginReq());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;

        // 如果是握手应答消息,需要判断是否认证成功
        if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
            byte loginResult = (byte) message.getBody();
            if (loginResult != (byte) 0) {
                // 握手失败,关闭连接
                ctx.close();
            } else {
                System.out.println("Login is ok : " + message);
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    private NettyMessage buildLoginReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        message.setHeader(header);
        return message;
    }
}
