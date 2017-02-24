package com.liuruichao.client.aio.handler;

import com.liuruichao.client.aio.MyClient1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

/**
 * WriteCompletionHandler
 *
 * @author liuruichao
 * @date 15/11/4 下午2:01
 */
public class WriteCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private MyClient1 client;

    public WriteCompletionHandler(MyClient1 client) {
        this.client = client;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        if (attachment.hasRemaining()) {
            client.getChannel().write(attachment, attachment, this);
        } else {
            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            client.getChannel().read(readBuffer, readBuffer, new ReadCompletionHandler(client));
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        client.stop();
    }
}
