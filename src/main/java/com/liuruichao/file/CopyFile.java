package com.liuruichao.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * CopyFile
 *
 * @author liuruichao
 * @date 15/7/17 下午10:29
 */
public class CopyFile {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        File inFile = new File("/Users/liuruichao/Documents/question.db");
        File outFile = new File("/Users/liuruichao/question.db.bak.back");

        OutputStream out = new FileOutputStream(outFile);

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
