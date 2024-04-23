package com.example.openapp;

public class ModelData {
    private String token;
    private String uId;

    public ModelData(String token, String uId) {
        this.token = token;
        this.uId = uId;
    }

    public ModelData() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
