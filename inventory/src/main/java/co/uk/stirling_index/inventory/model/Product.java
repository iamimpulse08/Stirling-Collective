package co.uk.stirling_index.inventory.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Table(name = "products")
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "business_id")
    private Business business;

    private String name;
    private String category;
    private Integer quantity;
    private Long price;
    private String image_uri;

    public Product() {}

    public Product(Product product) {
        this.id = product.id;
        this.business = product.business;
        this.name = product.name;
        this.category = product.category;
        this.quantity = product.quantity;
        this.price = product.price;
        this.image_uri = product.image_uri;
    }

    public Product(String name, Business business, String category, Integer quantity, Long price, String image_uri) {
        this.name = name;
        this.business = business;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.image_uri = image_uri;
    }

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
