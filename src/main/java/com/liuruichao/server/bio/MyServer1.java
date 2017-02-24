package com.liuruichao.server.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 单线程通信
 *
 * @author liuruichao
 * @date 15/7/18 下午2:03
 */
public class MyServer1 {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9999);
        Socket socket = server.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter outer = new PrintWriter(socket.getOutputStream());

        while (true) {
            System.out.println("client已连接.");
            String msg = reader.readLine();
            if ("bye".equals(msg)) {
                break;
            }
            outer.println("Server received " + msg);
            outer.flush();
        }

        socket.close();
    }
}
