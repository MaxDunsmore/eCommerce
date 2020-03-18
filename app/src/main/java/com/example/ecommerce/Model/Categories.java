package com.example.ecommerce.Model;

public class Categories {
    private String cname, image;

    public Categories() {
    }

    public Categories(String cname, String image) {
        this.cname = cname;
        this.image = image;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
