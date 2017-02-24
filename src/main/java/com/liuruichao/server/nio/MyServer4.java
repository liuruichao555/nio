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
 * 点对点聊天
 *
 * @author liuruichao
 * @date 15/7/23 下午11:38
 */
public class MyServer4 {
    private static final Logger logger = Logger.getLogger(MyServer4.class);
    private static SocketChannel client1 = null;
    private static SocketChannel client2 = null;

    public static void main(String[] args) {
        Selector selector = null;
        ServerSocketChannel server = null;
        try {
            selector = Selector.open();
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(NioConfig.PORT));
            server.register(selector, SelectionKey.OP_ACCEPT);
            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        SocketChannel channel = server.accept();
                        if (client1 == null) {
                            client1 = channel;
                        } else if (client2 == null) {
                            client2 = channel;
                        } else {
                            // 目前只接受两个连接（1对1）
                            channel.close();
                            logger.debug("超过两个连接");
                            continue;
                        }
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                        logger.debug("客户端连接事件");
                    } else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        logger.debug(channel);
                        int len = channel.read(buffer);
                        if (len != -1) {
                            String msg = new String(buffer.array(), 0, len);
                            if (channel == client1) {
                                client2.write(ByteBuffer.wrap(msg.getBytes()));
                            } else if (channel == client2){
                                client1.write(ByteBuffer.wrap(msg.getBytes()));
                            }
                           logger.debug("server : " + msg);
                        } else {
                            logger.debug("客户端关闭 : " + channel);
                            if (channel == client1) {
                                client1 = null;
                            } else {
                                client2 = null;
                            }
                            channel.close();
                        }
                        logger.debug("read事件");
                    } else if (key.isWritable()) {
                        logger.debug("write事件");
                    }
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
