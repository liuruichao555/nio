package com.liuruichao.file;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 文件读取
 *
 * @author liuruichao
 * @date 15/7/17 下午5:46
 */
public class ReadFile {
    public static void main(String[] args) throws Exception {
        File file = new File("/Users/liuruichao/Documents/server");
        FileInputStream fis = new FileInputStream(file);
        // 获取管道
        FileChannel channel = fis.getChannel();
        // 缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int len = -1;
        while ((len = channel.read(buffer)) != -1) {
            System.out.println(new String(buffer.array(), 0, len));
        }

        channel.close();
        fis.close();
    }
}
