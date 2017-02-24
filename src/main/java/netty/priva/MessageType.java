package netty.priva;

/**
 * Netty消息类型
 *
 * @author liuruichao
 * Created on 2015-12-11 13:51
 */
public enum MessageType {
    ERVICE_REQ((byte) 0), SERVICE_RESP((byte) 1), ONE_WAY((byte) 2), LOGIN_REQ(
            (byte) 3), LOGIN_RESP((byte) 4), HEARTBEAT_REQ((byte) 5), HEARTBEAT_RESP(
            (byte) 6);

    private byte value;

    private MessageType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }
}
