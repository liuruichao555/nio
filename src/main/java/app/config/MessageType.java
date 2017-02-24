package app.config;

/**
 * MessageType
 * 
 * @author liuruichao
 * Created on 2015-12-23 10:04
 */
public enum MessageType {
    LOGIN_REQ(1), LOGIN_RESP(2);

    private int value;
    MessageType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
