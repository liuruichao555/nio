package com.liuruichao.server.nio;

import com.liuruichao.config.NioConfig;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * MyServer3
 *
 * @author liuruichao
 * @date 15/7/23 上午10:23
 */
public class MyServer3 {
    private static final Logger logger = Logger.getLogger(MyServer3.class);

    public static void main(String[] args) {
        Selector selector = null;
        ServerSocketChannel server = null;
        try {
            selector = Selector.open();
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(NioConfig.PORT));
            // server 关心的是客户端连接事件
            server.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        SocketChannel channel = server.accept();
                        channel.configureBlocking(false);
                        channel.socket().setSoTimeout(1);
                        // 客户端的channel关心的是读写事件
                        channel.register(selector, SelectionKey.OP_READ);
                        logger.debug("客户端连接事件");
                    } else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        System.out.println(channel);

                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int len = channel.read(buffer);

                        logger.debug("len : " + len);
                        // len=-1 说明客户端关闭
                        if (len != -1) {
                            String msg = new String(buffer.array(), 0, len);
                            System.out.println("server : " + msg);
                            if (msg.equals("bye")) {
                                channel.close();
                                continue;
                            }
                            channel.write(ByteBuffer.wrap(msg.getBytes()));
                        } else {
                            // 客户端断开连接
                            logger.debug("客户端断开连接");
                            key.cancel();
                            channel.close();
                        }
                        logger.debug("read事件");
                    } else if (key.isWritable()) {
                        logger.debug("write事件");
                    } else if (key.isConnectable()) {
                        logger.debug("connect事件");
                    } else if (key.isValid()) {
                        logger.debug("valid事件");
                    }
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (server != null && server.isOpen()) {
                    server.close();
                }
                if (selector != null && selector.isOpen()) {
                    selector.close();
                }
            } catch (Exception e) {
            }
        }
    }
}
