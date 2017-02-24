package com.liuruichao.client.nio;

import com.liuruichao.config.NioConfig;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * MyClient3
 *
 * @author liuruichao
 * @date 15/7/23 上午11:24
 */
public class MyClient3 {
    private static final Logger logger = Logger.getLogger(MyClient3.class);

    public static void main(String[] args) throws Exception {
        connect();
    }

    public static void connect() throws Exception {
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(NioConfig.HOST_NAME, NioConfig.PORT));

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("请输入发送的消息：");
            String msg = reader.readLine();
            logger.debug("msg : " + msg.length());

            channel.write(ByteBuffer.wrap(msg.getBytes()));

            if ("bye".equals(msg)) {
                break;
            }

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int len = channel.read(buffer);
            if (len != -1) {
                System.out.println("client : " + new String(buffer.array(), 0, len));
            }
        }

        reader.close();
        channel.close();
    }
}
