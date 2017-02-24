package com.liuruichao.client.aio.handler;

import com.liuruichao.client.aio.MyClient1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * ReadCompletionHandler
 *
 * @author liuruichao
 * @date 15/11/4 下午2:00
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private MyClient1 client;

    public ReadCompletionHandler(MyClient1 client) {
        this.client = client;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] bytes = new byte[attachment.remaining()];
        attachment.get(bytes);
        String body = null;
        try {
            body = new String(bytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("msg : " + body);
        client.stop();
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        client.stop();
    }
}
