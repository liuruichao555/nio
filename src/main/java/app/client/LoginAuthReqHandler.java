package app.client;

import app.config.MessageType;
import app.model.Header;
import app.model.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.UUID;

/**
 * LoginAuthReqHandler
 * 
 * @author liuruichao
 * Created on 2015-12-23 13:39
 */
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLoginReq());
    }

    private Message buildLoginReq() {
        Message message = new Message();
        Header header = new Header();
        header.setType(MessageType.LOGIN_REQ.value());
        header.setPriority(0);
        header.setSessionId(UUID.randomUUID().toString().replace("-", ""));
        message.setHeader(header);
        message.setBody("刘瑞超");
        return message;
    }
}
