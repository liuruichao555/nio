package com.liuruichao.app.server;

import com.google.gson.Gson;
import com.liuruichao.app.dto.Result;
import com.liuruichao.app.utils.DataUtils;
import com.liuruichao.app.utils.TypeConvertUtils;
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
 * AppServer
 *
 * @author liuruichao
 * @date 15/11/2 下午2:30
 */
public class AppServer2 implements Runnable {
    private final Logger logger = Logger.getLogger(AppServer2.class);
    private Selector selector;
    private ServerSocketChannel server;
    private Gson gson = new Gson();
    private volatile boolean stop = false;

    public AppServer2(int port) {
        try {
            selector = Selector.open();
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));
            server.register(selector, SelectionKey.OP_ACCEPT);
            logger.debug("The server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            while (!stop) {
                int num = selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                SelectionKey key = null;
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    try {
                        handleInput(key);
                    } catch (Exception e) {
                        // 处理异常
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        stop = true;
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                SocketChannel channel = server.accept();
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ);
                logger.debug("客户端第一次连接。");
            }
            if (key.isReadable()) {
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
                        logger.debug("msg : " + msg);
                        // TODO 逻辑处理
                        process(clientChannel, msg);
                    }
                }
                if (len == -1) {
                    logger.debug("释放、关闭连接：" + clientChannel + "。");
                    key.cancel();
                    clientChannel.close();
                }
                logger.debug("read事件。");
            }
            if (key.isWritable()) {
                SocketChannel clientChannel = (SocketChannel) key.channel();
                String jsonStr = (String) key.attachment();
                ByteBuffer byteBuffer = ByteBuffer.wrap(DataUtils.addDataSize(jsonStr));
                clientChannel.write(byteBuffer);
                key.interestOps(SelectionKey.OP_READ);
                logger.debug("write事件。");
            }
            if (key.isConnectable()) {
                logger.debug("connect事件。");
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
        AppServer2 server = new AppServer2(NioConfig.PORT);
        new Thread(server).start();
    }
}
