package com.liuruichao.server.bio;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ssl
 * java -Djavax.net.ssl.keyStore=mysocket.jks -Djavax.net.ssl.keyStorePassword=liuruichao123 com.liuruichao.server.bio.MyServer5
 *
 * @author liuruichao
 * @date 15/7/19 下午3:46
 */
public class MyServer5 {
    public static void main(String[] args) throws Exception {
        ServerSocketFactory factory = SSLServerSocketFactory.getDefault();
        ServerSocket server = factory.createServerSocket(9999);
        Socket socket = server.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter outer = new PrintWriter(socket.getOutputStream());

        String msg = reader.readLine();
        System.out.println("msg : " + msg);
        outer.println("收到");
        outer.flush();

        outer.close();
        reader.close();
    }
}
