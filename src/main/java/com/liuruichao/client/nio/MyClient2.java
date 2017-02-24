package com.liuruichao.client.nio;

import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * MyClient2
 *
 * @author liuruichao
 * @date 15/7/21 下午5:58
 */
public class MyClient2 {
    private static final Logger logger = Logger.getLogger(MyClient2.class);

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10000; i++) {
            connect();
        }
    }

    public static void connect() throws Exception {
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress("localhost", 9999));
        ByteBuffer buffer = ByteBuffer.wrap("liuruichao".getBytes());
        channel.write(buffer);
        buffer = ByteBuffer.allocate(1024);
        int len = channel.read(buffer);
        if (len != -1) {
            logger.debug(new String(buffer.array(), 0, len));
        }
        channel.finishConnect();
        channel.close();
    }
}
