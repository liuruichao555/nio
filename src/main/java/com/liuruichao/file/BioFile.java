package com.liuruichao.file;

import java.io.*;

/**
 * BioFile
 * 2.6 G : 15267
 * @author liuruichao
 * @date 15/7/17 下午10:19
 */
public class BioFile {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
//        File inFile = new File("/Users/liuruichao/Documents/question.db");
        File inFile = new File("/Users/liuruichao/java_dump/focus_www.dump");
        File outFile = new File("/Users/liuruichao/question.db.bak.back");

        InputStream in = new FileInputStream(inFile);
        OutputStream out = new FileOutputStream(outFile);

        byte[] b = new byte[1024];
        int len = -1;
        while ((len = in.read(b)) != -1) {
            out.write(b, 0, len);
        }

        out.close();
        in.close();

        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
