package com.liuruichao.client.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * MyClient2
 *
 * @author liuruichao
 * @date 15/7/18 下午3:11
 */
public class MyClient2 {
    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        Socket socket = new Socket("localhost", 9999);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter outer = new PrintWriter(socket.getOutputStream());

        while (true) {
            String msg = input.next();
            outer.println(msg);
            outer.flush();
            if ("bye".equals(msg)) {
                break;
            }
            System.out.println("Server : " + reader.readLine());
        }

        outer.close();
        reader.close();
    }
}
