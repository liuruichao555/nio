package com.liuruichao.app.server;

import com.google.gson.Gson;
import com.liuruichao.app.utils.DataUtils;
import com.liuruichao.app.utils.TypeConvertUtils;
import com.liuruichao.app.dto.Result;
import com.liuruichao.config.NioConfig;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * AppServer v1
 *
 * @author liuruichao
 * @date 15/11/2 下午2:30
 */
public class AppServer {
    private final String charsetName = "utf-8";
    private final Logger logger = Logger.getLogger(AppServer.class);
    private Selector selector;
    private ServerSocketChannel server;
    private Gson gson = new Gson();

    public AppServer(int port) throws IOException {
        selector = Selector.open();
        server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(port));
        server.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void start() throws IOException {
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isValid()) {
                    if (key.isAcceptable()) {
                        SocketChannel channel = server.accept();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                        logger.debug("客户端第一次连接。");
                    } else if (key.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(4);
                        int len = clientChannel.read(buffer);
                        if (len != -1) {
                            // 获取客户端发来的数据大小
                            byte[] data = buffer.array();
                            int dataSize = TypeConvertUtils.byte2int(data);
                            // 获取客户端发来的数据
                            buffer = ByteBuffer.allocate(dataSize);
                            len = clientChannel.read(buffer);
                            if (len != -1) {
                                String msg = new String(buffer.array(), 0, len);
                                logger.debug("server , dataSize : " + dataSize + " data : " + msg);
                                // TODO 逻辑处理
                                process(clientChannel, msg);
                            }
                        }
                        // Selector的select方法返回的key集合中有一个SelectionKey是可读的，
                        // 但是调用与此SelectionKey关联的channel的read方法，总是返回读取长度是-1。
                        // 既然返回-1,可以说明tcp链接已经断开。
                        // 在下次调用select方法不应再返回这个SelectionKey，也不应该此SelectionKey是可读状态的。
                        // 但事实并非如此：java nio 会认为是可读状态
                        // 结果：读取的长度如果是-1，那么客户端断开连接，服务器端应该也断开连接。
                        if (len == -1) {
                            logger.debug("关闭连接。");
                            key.cancel();
                            clientChannel.close();
                        }
                        logger.debug("read事件。");
                    } else if (key.isWritable()) {
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        String jsonStr = (String) key.attachment();
                        ByteBuffer byteBuffer = ByteBuffer.wrap(DataUtils.addDataSize(jsonStr));
                        clientChannel.write(byteBuffer);
                        key.interestOps(SelectionKey.OP_READ);
                        logger.debug("write事件。");
                    } else if (key.isConnectable()) {
                        logger.debug("connect事件。");
                    }
                }
            }
        }
    }

    private void process(SocketChannel clientChannel, String msg) throws ClosedChannelException {
        Result result = null;
        switch (msg) {
            case "liuruichao":
                result = new Result(1, 1, 2, "liuruichao", "buzhidao");
                break;
            case "buzhidao":
                result = new Result(1, 2, 1, "buzhidao", "liuruichao");
                break;
            default:
                result = new Result(0);
        }
        String jsonStr = gson.toJson(result);
        clientChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, jsonStr);
    }

    public static void main(String[] args) throws Exception {
        AppServer server = new AppServer(NioConfig.PORT);
        server.start();
    }
}
