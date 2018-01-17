package json;

import lombok.Data;

/**
 * User
 *
 * @author liuruichao
 * Created on 2015-12-08 16:21
 */
@Data
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
}
