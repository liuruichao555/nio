package com.liuruichao.file;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 写文件
 *
 * @author liuruichao
 * @date 15/7/17 下午5:55
 */
public class WriteFile {
    public static void main(String[] args) throws Exception {
        String content = "liuruichao123\nliuruichao456";

        File file = new File("/Users/liuruichao/temp123.txt");
        FileOutputStream fos = new FileOutputStream(file);
        FileChannel channel = fos.getChannel();

        ByteBuffer buffer = ByteBuffer.wrap(content.getBytes());
        channel.write(buffer);
        fos.flush();

        channel.close();
        fos.close();
    }
}
