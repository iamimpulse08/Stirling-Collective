package co.uk.stirling_index.inventory.model;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "business_id")
    private UUID business_id;
    private String category;
    private int stock_count;
    private double price;
    private String imageURI;

    public Product() {}

    public Product(int id, String category, int stock_count, double price) {
        this.id = id;
        this.category = category;
        this.stock_count = stock_count;
        this.price = price;

    }

    public Product(Product product) {
        this(product.id, product.category, product.stock_count, product.price);
    }


    // getters

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public int getStockCount() {
        return stock_count;
    }

    public double getPrice() {
        return price;
    }


    // setters

    public void setStockCount(int stock_count) {
        this.stock_count = stock_count;
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
                + stock_count + ", price=" + price + "]";
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
