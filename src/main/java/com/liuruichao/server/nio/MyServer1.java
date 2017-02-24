package com.liuruichao.server.nio;


import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Collection;
import java.util.Iterator;

/**
 * MyServer1
 *
 * @author liuruichao
 * @date 15/7/18 下午2:07
 */
public class MyServer1 {
    private static final Logger logger = Logger.getLogger(MyServer1.class);

    public static void main(String[] args) {
        Selector selector = null;
        ServerSocketChannel serverSocketChannel = null;
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.socket().bind(new InetSocketAddress(9999));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isConnectable()) {
                        logger.debug("connectable");
                    } else if (key.isAcceptable()) {
                        print(selector.keys());
                        // 获得客户端连接通道
                        SocketChannel clientChannel = serverSocketChannel.accept();
                        clientChannel.configureBlocking(false);
                        // 向客户端发送消息
                        clientChannel.write(ByteBuffer.wrap("hello client".getBytes()));
                        clientChannel.register(selector, SelectionKey.OP_READ);

                        logger.debug("客户端连接事件。size : " + selector.keys().size());
                    } else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int len = channel.read(buffer);
                        if (len != -1) {
                            String msg = new String(buffer.array());
                            System.out.println("msg : " + msg);
                        }
//                        channel.register(selector, SelectionKey.OP_CONNECT);
                        channel.close();
                    } else if (key.isValid()) {
                        logger.debug("valid");
                    } else if (key.isWritable()) {
                        logger.debug("writable");
                    }
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (selector != null && selector.isOpen()) {
                    selector.close();
                }
                if (serverSocketChannel != null && serverSocketChannel.isOpen()) {
                    serverSocketChannel.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void print(Collection<SelectionKey> collection) {
        Iterator<SelectionKey> iterator = collection.iterator();
        logger.debug("---------------------------------");
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            logger.debug(key);
        }
        logger.debug("---------------------------------");
    }
}
