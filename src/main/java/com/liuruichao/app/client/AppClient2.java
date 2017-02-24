package com.liuruichao.app.client;

import com.liuruichao.app.utils.DataUtils;
import com.liuruichao.app.utils.TypeConvertUtils;
import com.liuruichao.config.NioConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AppClient
 *
 * @author liuruichao
 * @date 15/11/2 下午2:30
 */
public class AppClient2 implements Runnable {
    private final String charsetName = "utf-8";
    private final String EOF_FLAG = "bye";
    private final Logger logger = Logger.getLogger(AppClient2.class);
    private Selector selector;
    private SocketChannel channel;
    private String host;
    private int port;
    private volatile boolean stop = false;

    public AppClient2(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            selector = Selector.open();
            channel = SocketChannel.open();
            channel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            doConnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (!stop) {
            try {
                if (selector.isOpen()) {
                    // 到达事件的数量
                    int num = selector.select();
                    if (num > 0) {
                        Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            iterator.remove();
                            try {
                                handleInput(key);
                            } catch (Exception e) {
                                // 异常处理
                                if (key != null) {
                                    key.cancel();
                                    if (key.channel() != null) {
                                        key.channel().close();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        logger.debug(1);
        if (key.isValid()) {
            // 连接就绪
            if (channel.finishConnect()) {
                if (key.isReadable()) {
                    logger.debug("read事件。");
                    // 获取服务器返回的信息
                    String msg = readMsg();
                    // TODO 逻辑处理
                    System.out.println(msg);

                    if (EOF_FLAG.equals(msg)) {
                        // 对端链路关闭
                        key.cancel();
                        channel.close();
                    }
                }
                if (key.isWritable()) {
                    logger.debug("write事件。");
                    String msg = (String) key.attachment();
                    ByteBuffer buffer = ByteBuffer.wrap(DataUtils.addDataSize(msg));
                    channel.write(buffer);
                    channel.register(selector, SelectionKey.OP_READ);
                }
                if (key.isConnectable()) {
                    logger.debug("connect事件。");
                    channel.register(selector, SelectionKey.OP_READ);
                }
            }
        }
    }

    public void sendMsg(String msg) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(DataUtils.addDataSize(msg));
        channel.write(buffer);
        //channel.register(selector, SelectionKey.OP_WRITE, msg);
    }

    public void stop() {
        stop = true;
    }

    private String readMsg() throws IOException {
        String result = "";

        ByteBuffer buffer = ByteBuffer.allocate(4);
        // 获取服务端返回的数据长度
        int len = channel.read(buffer);
        if (len != -1) {
            byte[] data = buffer.array();
            int dataSize = TypeConvertUtils.byte2int(data);
            // 获取服务端返回的数据
            buffer = ByteBuffer.allocate(dataSize);
            len = channel.read(buffer);
            if (len != -1) {
                result = new String(buffer.array(), 0, dataSize);
            }
        }

        // 连接断开
        if (len == -1) {
            result = EOF_FLAG;
        }

        return result;
    }

    private void doConnect() throws IOException {
        if (channel.connect(new InetSocketAddress(host, port))) {
            logger.debug("client register OP_READ event.");
            channel.register(selector, SelectionKey.OP_READ);
        } else {
            logger.debug("client register OP_CONNECT event.");
            channel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        AppClient2 client = new AppClient2(NioConfig.HOST_NAME, NioConfig.PORT);
        new Thread(client).start();

        /*client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");*/

        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("请输入：");
            String msg = in.next();
            if (StringUtils.isEmpty(msg)) {
                System.out.println("输入为空。");
                continue;
            }
            if ("bye".equals(msg)) {
                System.out.println("客户端关闭");
                client.stop();
                break;
            }
            client.sendMsg(msg);
        }
    }
}
