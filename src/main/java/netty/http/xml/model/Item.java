package netty.http.xml.model;

/**
 * Item
 *
 * @author liuruichao
 *         Created on 2015-12-09 14:56
 */
public class Item {
    private String id;
    private String description;
    private int quantity;
    private float price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
