package com.liuruichao.server.aio.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * WriteCompletionHandler
 *
 * @author liuruichao
 * @date 15/11/4 下午1:57
 */
public class WriteCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel channel;

    public WriteCompletionHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        // 如果没有发送完成，继续发送
        if (attachment.hasRemaining()) {
            channel.write(attachment, attachment, this);
        }
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
