package com.example.QAapp.DTO;

public class WebSocketConnRequest {

    private String udid;

    public WebSocketConnRequest(String udid) {
        this.udid = udid;
    }

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }
}
