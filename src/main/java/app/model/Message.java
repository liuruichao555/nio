package app.model;

/**
 * Message
 * 
 * @author liuruichao
 * Created on 2015-12-23 10:52
 */
public class Message {
    private Header header;
    private String body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message{" +
                "header=" + header +
                ", body='" + body + '\'' +
                '}';
    }
}
