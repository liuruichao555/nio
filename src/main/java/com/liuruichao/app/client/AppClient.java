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
 * AppClient v1
 *
 * @author liuruichao
 * @date 15/11/2 下午2:30
 */
public class AppClient {
    private final String charsetName = "utf-8";
    private final String EOF_FLAG = "bye";
    private final Logger logger = Logger.getLogger(AppClient.class);
    private Selector selector;
    private SocketChannel channel;
    private String hostName;
    private int port;
    private boolean isConn = false;
    private ExecutorService exec = Executors.newFixedThreadPool(1);

    public AppClient(String hostName, int port) throws IOException {
        this.hostName = hostName;
        this.port = port;
    }

    public void connect() throws IOException {
        selector = Selector.open();
        channel = SocketChannel.open(new InetSocketAddress(hostName, port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        isConn = true;
        exec.execute(() -> {
            while (true) {
                if (!isConn) {
                    break;
                }
                try {
                    if (selector.isOpen()) {
                        // 到达事件的数量
                        int num = selector.select();
                        if (num > 0) {
                            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                            while (iterator.hasNext()) {
                                SelectionKey key = iterator.next();
                                iterator.remove();
                                if (key.isReadable()) {
                                    logger.debug("read事件。");
                                    // 获取服务器返回的信息
                                    String msg = readMsg();
                                    if (EOF_FLAG.equals(msg)) {
                                        logger.info("server shutdown...client quit...");
                                        close();
                                        break;
                                    }
                                    // TODO 逻辑处理
                                    System.out.println(msg);
                                } else if (key.isWritable()) {
                                    logger.debug("write事件。");
                                    String msg = (String) key.attachment();
                                    ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes(charsetName));
                                    channel.write(buffer);
                                    key.interestOps(SelectionKey.OP_READ);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendMsg(String msg) throws IOException {
        if (!isConn) {
            throw new RuntimeException("连接失败，或者没有调用connect()方法");
        }
        ByteBuffer buffer = ByteBuffer.wrap(DataUtils.addDataSize(msg));
        channel.write(buffer);
        buffer.clear();
        //channel.register(selector, SelectionKey.OP_WRITE, msg);
    }

    public String readMsg() throws IOException {
        if (!isConn) {
            throw new RuntimeException("连接失败，或者没有调用connect()方法");
        }
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

    public void close() throws IOException {
        if (channel != null) {
            channel.close();
        }
        if (selector != null) {
            selector.close();
        }
        isConn = false;
        exec.shutdown();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        AppClient client = new AppClient(NioConfig.HOST_NAME, NioConfig.PORT);
        client.connect();

        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");
        client.sendMsg("liuruichao");

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
                client.close();
                break;
            }
            client.sendMsg(msg);
        }
    }
}
