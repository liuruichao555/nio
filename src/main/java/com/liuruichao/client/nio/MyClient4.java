package com.liuruichao.client.nio;

import com.liuruichao.config.NioConfig;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * 点对点聊天
 *
 * @author liuruichao
 * @date 15/7/23 下午11:46
 */
public class MyClient4 {
    private static final Logger logger = Logger.getLogger(MyClient4.class);

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Selector selector = Selector.open();
        SocketChannel channel =
                SocketChannel.open(new InetSocketAddress(NioConfig.HOST_NAME, NioConfig.PORT));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);

        new Thread(() -> {
            try {
                while (true) {
                    String msg = reader.readLine();
                    System.out.println("me : " + msg);
                    channel.write(ByteBuffer.wrap(msg.getBytes()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int len = channel.read(buffer);
                    if (len != -1) {
                        System.out.println("other : " + new String(buffer.array(), 0, len));
                    } else {
                        throw new IOException();
                    }
                    logger.debug("read");
                }
            }


//            System.out.print("请输入需要发送的消息：");
//            String msg = reader.readLine();
//            channel.write(ByteBuffer.wrap(msg.getBytes()));
//            if ("bye".equals(msg)) {
//                break;
//            }
//            ByteBuffer buffer = ByteBuffer.allocate(1024);
//            int len = channel.read(buffer);
//            if (len != -1) {
//                System.out.println(new String(buffer.array(), 0, len));
//            } else {
//                break;
//            }
        }

//        reader.close();
//        selector.close();
//        channel.close();
    }
}
