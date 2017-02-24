package com.liuruichao.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ReadWriteFile
 * time : 260 ~ 360 之间
 * 2.6 G : 15413 17107
 * @author liuruichao
 * @date 15/7/17 下午6:01
 */
public class ReadWriteFile2 {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
//        File inFile = new File("/Users/liuruichao/Documents/question.db");
        File inFile = new File("/Users/liuruichao/java_dump/focus_www.dump");
        File outFile = new File("/Users/liuruichao/question.db.bak.back");

        FileInputStream in = new FileInputStream(inFile);
        FileOutputStream out = new FileOutputStream(outFile);

        FileChannel inChannel = in.getChannel();
        FileChannel outChannel = out.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (inChannel.read(buffer) != -1) {
            buffer.flip();
            outChannel.write(buffer);
            buffer.clear();
        }

        out.close();
        in.close();

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
