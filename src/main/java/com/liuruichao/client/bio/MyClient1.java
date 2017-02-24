package com.liuruichao.client.bio;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * 单线程通信
 *
 * @author liuruichao
 * @date 15/7/18 下午2:02
 */
public class MyClient1 {
    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        Socket socket = new Socket("localhost", 9999);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter outer = new PrintWriter(socket.getOutputStream());

        while (true) {
            System.out.print("请输入：");
            String msg = input.next();
            outer.println(msg);
            outer.flush();
            if ("bye".equals(msg)) {
                break;
            }
            System.out.println(reader.readLine());
        }

        socket.close();
    }
}
