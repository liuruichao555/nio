package com.liuruichao.server.nio;

import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * MyServer2
 *
 * @author liuruichao
 * @date 15/7/21 下午5:46
 */
public class MyServer2 {
    private static final Logger logger = Logger.getLogger(MyServer2.class);
    public static void main(String[] args) {
        Selector selector = null;
        ServerSocketChannel serverChannel = null;
        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(9999));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();
                // 因为处理完需要删除，所以用迭代器
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        // 客户端连接事件
                        SocketChannel clientChannel = serverChannel.accept();
                        clientChannel.configureBlocking(false);
                        logger.debug(clientChannel);
                        clientChannel.register(selector, SelectionKey.OP_READ);
                        logger.debug("客户端连接事件。");
                    } else if (key.isReadable()) {
                        // 可读事件
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int len = clientChannel.read(buffer);
                        if (len != -1) {
                            logger.debug("server : " + new String(buffer.array(), 0, len));
                        }
                        clientChannel.write(ByteBuffer.wrap("server received.".getBytes()));
                        clientChannel.finishConnect();
                        clientChannel.close();
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
                if (serverChannel != null && serverChannel.isOpen()) {
                    serverChannel.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}