package co.uk.stirling_index.inventory.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.hateoas.server.core.Relation;

import java.util.Objects;
import java.util.UUID;

@Table(name = "products")
public class Product {

    @Id
    private int id;
    private int businessId;

    private String name;
    private String category;
    private int quantity;
    private double price;
    private String imageURI;

    public Product() {}

    public Product(Product product) {
        this.id = product.id;
        this.businessId = product.businessId;
        this.name = product.name;
        this.category = product.category;
        this.quantity = product.quantity;
        this.price = product.price;
        this.imageURI = product.imageURI;
    }

    public Product(String name, int businessId, String category, int quantity, double price, String imageURI) {
        this.name = name;
        this.businessId = businessId;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.imageURI = imageURI;
    }

    // getters

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public int getStockCount() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }


    // setters

    public void setStockCount(int stock_count) {
        this.quantity = stock_count;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // toString

    @Override
    public String toString() {
        return "Product [id=" + id + ", description=" + ", category=" + category + ", stock_count="
                + quantity + ", price=" + price + "]";
    }

    // equals

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Product other = (Product) obj;
        return Objects.equals(this.id, other.id);
    }
}
