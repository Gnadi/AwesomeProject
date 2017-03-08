package htl.at.awesomeproject.entity;

/**
 * Created by Gnadlinger on 19-Jan-17.
 */

public class Product {
    private String productname;
    private String quantity;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProductname() {
        return productname;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Product(String productname, String quantity) {
        this.productname = productname;
        this.quantity = quantity;
    }

    public Product(String productname, String quantity, String username) {
        this.productname = productname;
        this.quantity = quantity;
        this.username = username;
    }

    public Product() {
    }
}
