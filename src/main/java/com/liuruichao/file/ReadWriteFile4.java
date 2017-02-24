package com.liuruichao.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ReadWriteFile
 * time : 80 左右
 * map 方式拷贝大文件 报错： Size exceeds Integer.MAX_VALUE
 * @author liuruichao
 * @date 15/7/17 下午6:01
 */
public class ReadWriteFile4 {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        File inFile = new File("/Users/liuruichao/Documents/question.db");
        File outFile = new File("/Users/liuruichao/question.db.bak.back");

        FileInputStream in = new FileInputStream(inFile);
        FileOutputStream out = new FileOutputStream(outFile);

        FileChannel inChannel = in.getChannel();
        FileChannel outChannel = out.getChannel();

        // 文件写出
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        outChannel.write(buffer);

        outChannel.close();
        inChannel.close();

        out.close();
        in.close();

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
