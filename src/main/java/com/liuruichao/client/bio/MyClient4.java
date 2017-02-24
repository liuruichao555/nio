package com.liuruichao.client.bio;

import com.liuruichao.model.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * MyClient4
 *
 * @author liuruichao
 * @date 15/7/19 下午1:27
 */
public class MyClient4 {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 9999);
        GZIPOutputStream out = new GZIPOutputStream(socket.getOutputStream());
        ObjectOutputStream objOut = new ObjectOutputStream(out);

        User user = new User();
        user.setName("client");
        user.setAge(1);
        objOut.writeObject(user);
        objOut.flush();
        System.out.println("flush done");
        out.finish();
        System.out.println("finish ");

        GZIPInputStream in = new GZIPInputStream(socket.getInputStream());
        ObjectInputStream objIn = new ObjectInputStream(in);
        user = (User) objIn.readObject();
        System.out.println(user);

        objOut.close();
        objIn.close();
        out.close();
        in.close();
    }
}
