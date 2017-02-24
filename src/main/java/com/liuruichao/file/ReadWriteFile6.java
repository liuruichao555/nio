package com.liuruichao.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.*;

/**
 * ReadWriteFile
 * time : 100左右
 *
 * 2.6 G : 21943 12315 10676
 * @author liuruichao
 * @date 15/7/17 下午6:01
 */
public class ReadWriteFile6 {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

//        Path sourceFile = Paths.get("/Users/liuruichao/Documents/question.db");
//        Path destFile = Paths.get("/Users/liuruichao/question.db.bak.back");
        Path sourceFile = Paths.get("/Users/liuruichao/java_dump/focus_www.dump");
        Path destFile = Paths.get("/Users/liuruichao/test.dump");

        Files.copy(sourceFile, destFile,
                StandardCopyOption.REPLACE_EXISTING, LinkOption.NOFOLLOW_LINKS);

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
