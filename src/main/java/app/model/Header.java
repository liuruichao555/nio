package app.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Message Header
 * 
 * @author liuruichao
 * Created on 2015-12-23 10:13
 */
public class Header {
    private int length; // 消息长度
    private int crcCode = 1; // 消息版本
    private String sessionId; // 会话Id
    private int type; // 消息类型
    private int priority; // 消息优先等级

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Header{" +
                "length=" + length +
                ", crcCode=" + crcCode +
                ", sessionId='" + sessionId + '\'' +
                ", type=" + type +
                ", priority=" + priority +
                '}';
    }
}
