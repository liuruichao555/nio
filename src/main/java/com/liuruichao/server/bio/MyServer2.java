package com.liuruichao.server.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 多线程通信
 *
 * @author liuruichao
 * @date 15/7/18 下午3:01
 */
public class MyServer2 {
    private static final Executor exec = Executors.newCachedThreadPool();
    private static final List<Socket> clients = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9999);
        while (true) {
            final Socket client = server.accept();
            clientConn(client);
            System.out.println("当前用户：" + clients.size());
        }
    }

    public static void clientConn(final Socket client) {
        clients.add(client);
        exec.execute(() -> {
            BufferedReader reader = null;
            PrintWriter outer = null;
            try {
                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                outer = new PrintWriter(client.getOutputStream());
                while (true) {
                    String msg = reader.readLine();
                    if ("bye".equals(msg)) {
                        break;
                    }
                    System.out.println(client + " send msg : " + msg);
                    outer.println("Server received " + msg);
                    outer.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (outer != null) outer.close();
                    if (reader != null) reader.close();
                    if (client != null) client.close();
                    if (clients.contains(client)) {
                        clients.remove(client);
                        System.out.println("当前用户：" + clients.size());
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        });
    }
}
