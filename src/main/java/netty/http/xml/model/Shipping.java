package netty.http.xml.model;

/**
 * Shipping
 *
 * @author liuruichao
 * Created on 2015-12-08 16:40
 */
public class Shipping {
    private Integer id;
    private String description;
    //STANDARD_MAIL, PRIORITY_MAIL, INTERNATIONAL_MAIL, DOMESTIC_EXPRESS, INTERNATIONAL_EXPRESS


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
