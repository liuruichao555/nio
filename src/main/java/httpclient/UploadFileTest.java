package httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * UploadFileTest
 * 
 * @author liuruichao
 * Created on 2016-01-15 15:16
 */
public class UploadFileTest {
    public static void main(String[] args) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("http://img.51universe.com/upload/img");
        FileBody fileBody = new FileBody(new File("/Users/liuruichao/Downloads/nicai.jpg"));
        FileBody fileBody2 = new FileBody(new File("/Users/liuruichao/Downloads/IMG_0381.PNG"));
        StringBody descBody = new StringBody("文件描述.", ContentType.MULTIPART_FORM_DATA);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addPart("file", fileBody);
        builder.addPart("file2", fileBody2);
        builder.addPart("desc", descBody);
        httpPost.setEntity(builder.build());
        HttpResponse response = httpClient.execute(httpPost);
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
        } else {
            System.out.println("服务器错误. ");
        }
    }
}
