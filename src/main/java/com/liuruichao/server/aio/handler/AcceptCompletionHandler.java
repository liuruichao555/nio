package com.liuruichao.server.aio.handler;

import com.liuruichao.server.aio.MyServer1;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * AcceptCompletionHandler
 *
 * @author liuruichao
 * @date 15/11/4 下午1:55
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, MyServer1> {
    @Override
    public void completed(AsynchronousSocketChannel result, MyServer1 attachment) {
        attachment.getServerSocketChannel().accept(attachment, this);

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, MyServer1 attachment) {
        exc.printStackTrace();
        attachment.stop();
    }
}
