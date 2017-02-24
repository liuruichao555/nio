package app.server;

import app.model.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录响应
 * 
 * @author liuruichao
 * Created on 2015-12-23 10:01
 */
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {
    private Map<String, Boolean> nodeCheck = new ConcurrentHashMap<>(); // 登录缓存
    private String[] whiteList = {"127.0.0.1"}; // 白名单

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        System.out.println(message);
    }
}
