package json;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * FastJsonTest
 * 1392
 397
 444
 560
 238
 203
 420
 211
 364
 528

 7490
 3648
 3252
 4001
 3664
 3064
 3545
 4250
 * @author liuruichao
 *         Created on 2015-12-08 16:23
 */
public class FastJsonTest {
    public static void main(String[] args) {
        List<User> list = initUser();

        for (int i = 0; i < 10; i++) {
            long startTime = System.currentTimeMillis();
            String jsonStr = JSON.toJSONString(list);
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
