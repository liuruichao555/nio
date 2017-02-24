package com.liuruichao.server.bio;

import com.liuruichao.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * socket发送对象
 *
 * @author liuruichao
 * @date 15/7/18 下午4:03
 */
public class MyServer3 {
    private static boolean isBreak = false;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket server = new ServerSocket(9999);
        Socket socket = server.accept();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

        User user = (User) in.readObject();
        System.out.println(user);
        user.setName("buzhidao");
        user.setAge(100);
        out.writeObject(user);
        out.flush();

        out.close();
        in.close();
    }
}
