package com.liuruichao.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ReadWriteFile
 * time : 80 左右
 * 2.6 G : 9162 (实际上拷贝了2.0G, transferTo 最大支持2.0G), transferFrom没有这个限制
 * @author liuruichao
 * @date 15/7/17 下午6:01
 */
public class ReadWriteFile3 {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
//        File inFile = new File("/Users/liuruichao/Documents/question.db");
        File inFile = new File("/Users/liuruichao/java_dump/focus_www.dump");
        File outFile = new File("/Users/liuruichao/question.db.bak.back");

        FileInputStream in = new FileInputStream(inFile);
        FileOutputStream out = new FileOutputStream(outFile);

        FileChannel inChannel = in.getChannel();
        FileChannel outChannel = out.getChannel();

        // 文件写出
        inChannel.transferTo(0, inFile.length(), outChannel);

        outChannel.close();
        inChannel.close();

        out.close();
        in.close();

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
