package httpclient;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * Test1
 * 
 * @author liuruichao
 * Created on 2016-01-08 09:57
 */
public class Test1 {
    public static void main(String[] args) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://www.baidu.com");
        HttpResponse response = httpClient.execute(httpGet);
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            InputStream in = entity.getContent();
            int len;
            byte[] buffer = new byte[1024];
            StringBuffer sbu = new StringBuffer();
            while ((len = in.read(buffer)) != -1) {
                sbu.append(new String(buffer, 0, len));
            }
            System.out.println(sbu.toString());
        }
    }
}
