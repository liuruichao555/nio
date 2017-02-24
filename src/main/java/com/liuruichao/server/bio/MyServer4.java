package com.liuruichao.server.bio;

import com.liuruichao.model.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP 传输数据 (单点)
 *
 * @author liuruichao
 * @date 15/7/19 下午12:46
 */
public class MyServer4 {
    public static void main(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(9999);
        Socket socket = server.accept();
        System.out.println("客户端已连接。");
        GZIPInputStream in = new GZIPInputStream(socket.getInputStream());
        ObjectInputStream objIn = new ObjectInputStream(in);
        GZIPOutputStream out = new GZIPOutputStream(socket.getOutputStream());
        ObjectOutputStream objOut = new ObjectOutputStream(out);

        User user = (User) objIn.readObject();
        System.out.println(user);
        user.setName("server");
        user.setAge(1000);
        objOut.writeObject(user);
        objOut.flush();
        out.finish();

        objOut.close();
        out.close();
        objIn.close();
        in.close();
    }
}
