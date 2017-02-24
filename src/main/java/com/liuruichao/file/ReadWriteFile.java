package com.liuruichao.file;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;

/**
 * ReadWriteFile
 * time : 380 ~ 420 之间
 * @author liuruichao
 * @date 15/7/17 下午6:01
 */
public class ReadWriteFile {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        File inFile = new File("/Users/liuruichao/Documents/question.db");
        File outFile = new File("/Users/liuruichao/question.db.bak.back");

        FileInputStream in = new FileInputStream(inFile);
        FileOutputStream out = new FileOutputStream(outFile);

        FileChannel inChannel = in.getChannel();
        FileChannel outChannel = out.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int len = -1;
        while ((len = inChannel.read(buffer)) != -1) {
            outChannel.write(ByteBuffer.wrap(buffer.array(), 0, len));
            buffer.clear();
        }
        out.flush();

        out.close();
        in.close();
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
