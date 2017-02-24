package httpclient;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test3
 * 
 * @author liuruichao
 * Created on 2016-01-08 10:26
 */
public class Test3 {
    public static void main(String[] args) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://python.lsuperman.com/test.php");

        // build request params
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("name", "刘瑞超"));
        params.add(new BasicNameValuePair("age", "20"));
        httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

        // response
        HttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            String result = CommonUtils.streamToStr(entity.getContent());
            Gson gson = new Gson();
            User user = gson.fromJson(result, User.class);
            System.out.println(String.format("name : %s, age : %s.", user.getName(), user.getAge()));
        } else {
            System.out.println("请求失败!");
        }
    }
}
