package com.liuruichao.client.aio;

import com.liuruichao.client.aio.handler.WriteCompletionHandler;
import com.liuruichao.config.NioConfig;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * MyClient1
 *
 * @author liuruichao
 * @date 15/11/4 下午1:01
 */
public class MyClient1 implements CompletionHandler<Void, MyClient1>, Runnable {
    private AsynchronousSocketChannel channel;
    private String host;
    private int port;
    private CountDownLatch latch;

    public MyClient1(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            channel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void completed(Void result, MyClient1 attachment) {
        try {
            byte[] bytes = "liuruichao".getBytes("utf-8");
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer, writeBuffer, new WriteCompletionHandler(this));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failed(Throwable exc, MyClient1 attachment) {
        exc.printStackTrace();
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        latch.countDown();
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        channel.connect(new InetSocketAddress(host, port), this, this);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        latch.countDown();
    }

    public AsynchronousSocketChannel getChannel() {
        return channel;
    }

    public static void main(String[] args) {
        MyClient1 client = new MyClient1(NioConfig.HOST_NAME, NioConfig.PORT);
        new Thread(client).start();
    }
}
