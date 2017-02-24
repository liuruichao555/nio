package json;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * GsonTest
 *
 * 767
 530
 439
 393
 280
 428
 327
 400
 383
 349


 5876
 4445
 4007
 3559
 3101
 3046
 2615
 2816
 2671
 3403
 * @author liuruichao
 *         Created on 2015-12-08 16:23
 */
public class GsonTest {
    public static void main(String[] args) {
        Gson gson = new Gson();
        List<User> list = initUser();

        for (int i = 0; i < 10; i++) {
            long startTime = System.currentTimeMillis();
            String jsonStr = gson.toJson(list);
            long endTime = System.currentTimeMillis();
            System.out.println(endTime - startTime);
        }
    }

    private static List<User> initUser() {
        List<User> list = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            list.add(new User(i, "liuruichao" + i, "liuruichao", "liuruichao", "liuruichao", 1, "all"));
        }
        return list;
    }
}
