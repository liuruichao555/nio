package com.liuruichao.client.bio;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ssl
 * java -Djavax.net.ssl.trustStore=mysocket.jks  -Djavax.net.ssl.trustStorePassword=liuruichao123 com.liuruichao.client.bio.MyClient5
 *
 * @author liuruichao
 * @date 15/7/19 下午3:46
 */
public class MyClient5 {
    public static void main(String[] args) throws Exception {
        SocketFactory factory = SSLSocketFactory.getDefault();
        Socket socket = factory.createSocket("localhost", 9999);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter outer = new PrintWriter(socket.getOutputStream());

        outer.println("liuruichao");
        outer.flush();
        String msg = reader.readLine();
        System.out.println("server recived : " + msg);

        outer.close();
        reader.close();
    }
}
