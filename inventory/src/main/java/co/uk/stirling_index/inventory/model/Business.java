package co.uk.stirling_index.inventory.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "Businesses")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID business_id;

    @Column
    private String name;

    @Column
    private String address;

    @Column
    private String postcode;


    public UUID getBusiness_id() {
        return business_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLogoURI() {
        return logoURI;
    }

    public void setLogoURI(String logoURI) {
        this.logoURI = logoURI;
    }

    private String phone;
    private String email;
    private String website;
    private String logoURI;






}
