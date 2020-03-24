package com.example.ecommerce;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.ecommerce.BR;

public class User extends BaseObservable {

    private String name;
    private String Password;
    private String phoneNumber;
    private String image;
    private String address;

    public User() {

    }

    public User(String name, String password, String phoneNumber, String image, String address, String dbName) {
        this.name = name;
        Password = password;
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.address = address;
        this.dbName  = dbName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    private String dbName;

}
