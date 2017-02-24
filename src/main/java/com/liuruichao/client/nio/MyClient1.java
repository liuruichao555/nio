package com.liuruichao.client.nio;

import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * MyClient1
 *
 * @author liuruichao
 * @date 15/7/18 下午2:06
 */
public class MyClient1 {
    private static final Logger logger = Logger.getLogger(MyClient1.class);
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    connectServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public static void connectServer() throws Exception {
        for (int i = 0; i < 1000; i++) {
            SocketChannel channel = SocketChannel.open();
            channel.connect(new InetSocketAddress("localhost", 9999));
            ByteBuffer buffer = ByteBuffer.wrap("liuruichao".getBytes());
            channel.write(buffer);
            buffer = ByteBuffer.allocate(1024);
            channel.read(buffer);
            logger.debug(new String(buffer.array()));
            logger.debug("done.");
        }
    }
}
