package netty.http.xml.codec;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * HttpXmlRequest
 *
 * @author liuruichao
 * Created on 2015-12-09 14:27
 */
public class HttpXmlRequest {
    private FullHttpRequest request;
    private Object body;

    public HttpXmlRequest(FullHttpRequest request, Object body) {
        this.request = request;
        this.body = body;
    }

    public FullHttpRequest getRequest() {
        return request;
    }

    public void setRequest(FullHttpRequest request) {
        this.request = request;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
