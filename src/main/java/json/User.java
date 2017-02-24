package json;

/**
 * User
 *
 * @author liuruichao
 * Created on 2015-12-08 16:21
 */
public class User {
    private Integer id;
    private String userName;
    private String password;
    private String address;
    private String realName;
    private Integer isBind;
    private String member;

    public User(Integer id, String userName, String password, String address, String realName, Integer isBind, String member) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.address = address;
        this.realName = realName;
        this.isBind = isBind;
        this.member = member;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getIsBind() {
        return isBind;
    }

    public void setIsBind(Integer isBind) {
        this.isBind = isBind;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}
