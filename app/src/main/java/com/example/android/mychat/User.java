package com.example.android.mychat;

import java.io.Serializable;

public class User implements Serializable {
    private String username,password,email,mobile,address,picUrl;
    public User(){}

    public User(String username, String password, String email, String mobile, String address, String picUrl) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
        this.picUrl = picUrl;
    }

    public User(String username, String picUrl) {
        this.username = username;
        this.picUrl = picUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User(String username, String password, String email, String mobile, String address ) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.mobile=mobile;
        this.address=address;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
