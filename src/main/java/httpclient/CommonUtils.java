package httpclient;

import java.io.IOException;
import java.io.InputStream;

/**
 * CommonUtils
 *
 * @author liuruichao
 * Created on 2016-01-08 10:11
 */
public class CommonUtils {
    public static String streamToStr(InputStream in) throws IOException {
        int len;
        byte[] buffer = new byte[1024];
        StringBuffer sbu = new StringBuffer();
        while ((len = in.read(buffer)) != -1) {
            sbu.append(new String(buffer, 0, len));
        }
        return sbu.toString();
    }
}
