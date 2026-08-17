package co.uk.stirling_index.inventory.model.business;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Table(name = "businesses")
@Entity
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String email;
    private String address;
    private String postcode;
    private String phone;
    private String website;
    private String logo_uri;


    public Business() {}

    public Business(Business business) {
        this.id = business.id;
        this.name = business.name;
        this.address = business.address;
        this.postcode = business.postcode;
        this.phone = business.phone;
        this.email = business.email;
        this.website = business.website;
        this.logo_uri = business.logo_uri;
    }

    public Business(String name, String address, String postcode, String email, String phone) {
        this.name = name;
        this.address = address;
        this.postcode = postcode;
        this.phone = phone;
        this.email = email;
        this.website = "";
        this.logo_uri = "";
    }








}
