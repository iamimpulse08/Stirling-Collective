package co.uk.stirling_index.inventory.model;

import jakarta.annotation.Generated;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "businesses")
public class Business {

    @Id
    private int id;
    private String name;
    private String email;
    private String address;
    private String postcode;
    private String phone;
    private String website;
    private String logoURI;


    public Business() {}

    public Business(Business business) {
        this.id = business.id;
        this.name = business.name;
        this.address = business.address;
        this.postcode = business.postcode;
        this.phone = business.phone;
        this.email = business.email;
        this.website = business.website;
        this.logoURI = business.logoURI;
    }

    public Business(String name, String address, String postcode, String email, String phone) {
        this.name = name;
        this.address = address;
        this.postcode = postcode;
        this.phone = phone;
        this.email = email;
        this.website = "";
        this.logoURI = "";
    }

    public int getBusiness_id() {
        return id;
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







}
