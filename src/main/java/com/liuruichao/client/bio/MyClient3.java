package com.liuruichao.client.bio;

import com.liuruichao.model.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * MyClient3
 *
 * @author liuruichao
 * @date 15/7/18 下午4:27
 */
public class MyClient3 {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 9999);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        User user = new User();
        user.setName("liuruichao");
        user.setAge(20);
        out.writeObject(user);
        out.flush();

        user = (User) in.readObject();
        System.out.println("client : " + user);

        out.close();
        in.close();
    }

}
