package netty.priva.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Header
 *
 * @author liuruichao
 * Created on 2015-12-11 10:16
 */
public final class Header {
    private int crcCode = 0xabef0101;
    private int length; // 消息长度
    private long sessionId; // 会话Id
    private byte type; // 消息类型
    private byte priority; // 消息优先等级
    private Map<String, Object> attachment = new HashMap<>(); // 附件,可以扩充字段

    public int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "Header{" +
                "crcCode=" + crcCode +
                ", length=" + length +
                ", sessionId=" + sessionId +
                ", type=" + type +
                ", priority=" + priority +
                ", attachment=" + attachment +
                '}';
    }
}