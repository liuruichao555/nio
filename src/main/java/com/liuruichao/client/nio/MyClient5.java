package com.liuruichao.client.nio;

import com.liuruichao.config.NioConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 *
 *
 * @author liuruichao
 * @date 15/7/24 下午4:38
 */
public class MyClient5 {
    public static void main(String[] args) throws Exception {
        boolean isStop = false;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(NioConfig.HOST_NAME, NioConfig.PORT));

        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    String msg = reader.readLine();
                    System.out.println("me : " + msg);
                    channel.write(ByteBuffer.wrap(msg.getBytes()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int len = channel.read(buffer);
            if (len != -1) {
                System.out.println("other : " + new String(buffer.array(), 0, len));
            } else {
                thread.interrupt();
                break;
            }
        }

        reader.close();
        channel.close();
    }
}
