package com.liuruichao.server.aio;

import com.liuruichao.config.NioConfig;
import com.liuruichao.server.aio.handler.AcceptCompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * MyServer1
 *
 * @author liuruichao
 * @date 15/11/4 下午1:00
 */
public class MyServer1 implements Runnable {
    private int port;
    private CountDownLatch countDownLatch;
    AsynchronousServerSocketChannel serverSocketChannel;

    private MyServer1(int port) {
        this.port = port;
        try {
            serverSocketChannel = AsynchronousServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("The server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        countDownLatch = new CountDownLatch(1);
        serverSocketChannel.accept(this, new AcceptCompletionHandler());
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        countDownLatch.countDown();
    }

    public AsynchronousServerSocketChannel getServerSocketChannel() {
        return serverSocketChannel;
    }

    public static void main(String[] args) {
        MyServer1 server = new MyServer1(NioConfig.PORT);
        new Thread(server).start();
    }
}
