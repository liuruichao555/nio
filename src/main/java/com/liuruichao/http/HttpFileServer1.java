package com.liuruichao.http;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;

/**
 * HttpFileServer1
 *
 * @author liuruichao
 * @date 15/11/4 下午3:37
 */
public class HttpFileServer1 implements Runnable {
    private final Logger logger = Logger.getLogger(HttpFileServer1.class);
    private int port;
    private Selector selector;
    private ServerSocketChannel server;
    private boolean stop = true;

    public HttpFileServer1(int port) {
        this.port = port;
        try {
            selector = Selector.open();
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));
            server.register(selector, SelectionKey.OP_ACCEPT);
            stop = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (!stop) {
                int num = selector.select();
                if (num > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    SelectionKey key = null;
                    while (iterator.hasNext()) {
                        key = iterator.next();
                        iterator.remove();
                        if (key.isValid()) {
                            if (key.isAcceptable()) {
                                logger.debug("accept事件。");
                                SocketChannel client = server.accept();
                                client.configureBlocking(false);
                                client.register(selector, SelectionKey.OP_READ);
                            }
                            if (key.isReadable()) {
                                logger.debug("read事件。");
                                SocketChannel client = (SocketChannel) key.channel();
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                int len = client.read(buffer);
                                if (len != -1) {
                                    String msg = new String(buffer.array(), 0, len);
                                    logger.debug("msg : \n" + msg);
                                    // 输出内容
                                    Date curDate = new Date();
                                    String response = "HTTP/1.1 200 OK\r\n" +
                                            "Server: nicai\r\n" +
                                            "Date: Thu,08 Mar 200707:17:51 GMT\r\n" +
                                            "Connection: Keep-Alive\r\n" +
                                            "Content-Type: text/html\r\n" +
                                            "Expries: Thu,08 Mar 2007 07:16:51 GMT\r\n" +
                                            "Cache-Control: no-cache\r\n\r\n" +
                                            "<html><head><title>Hello World</title></head><body>Hello World!</body></html>";
                                    buffer = ByteBuffer.wrap(response.getBytes());
                                    client.write(buffer);
                                    client.close();
                                }
                                if (len == -1) {
                                    client.finishConnect();
                                    client.close();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        HttpFileServer1 server = new HttpFileServer1(9999);
        new Thread(server).start();
    }
}
