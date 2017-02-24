package httpclient;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

/**
 * Test2
 * 
 * @author liuruichao
 * Created on 2016-01-08 10:05
 */
public class Test2 {
    public static void main(String[] args) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://python.lsuperman.com/test1.php?name=刘瑞超&age=20");
        HttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200) {
            String result = CommonUtils.streamToStr(response.getEntity().getContent());
            Gson gson = new Gson();
            User user = gson.fromJson(result, User.class);
            System.out.println(String.format("name : %s, age : %s.", user.getName(), user.getAge()));
        } else {
            System.out.println("请求失败.");
        }
    }
}
