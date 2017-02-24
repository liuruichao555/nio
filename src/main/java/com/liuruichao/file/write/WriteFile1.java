package com.liuruichao.file.write;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * WriteFile1
 *
 * @author liuruichao
 * @date 15/11/2 下午2:25
 */
public class WriteFile1 {
    public static void main(String[] args) throws Exception {
        FileInputStream in = new FileInputStream(new File("/Users/liuruichao/temp/test"));
        FileOutputStream out = new FileOutputStream(new File("/Users/liuruichao/temp/test2"));

        out.getChannel().transferFrom(in.getChannel(), 0, in.getChannel().size());

        out.close();
        in.close();
    }
}
