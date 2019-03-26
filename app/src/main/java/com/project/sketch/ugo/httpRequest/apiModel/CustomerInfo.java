package com.project.sketch.ugo.httpRequest.apiModel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Developer on 1/23/18.
 */

public class CustomerInfo {


    @SerializedName("uid")
    private String uid;



    @SerializedName("deviceid")
    private String deviceid;


    @SerializedName("name")
    private String name;



    @SerializedName("device_type")
    private String device_type;




    @SerializedName("fcm_reg_token")
    private String fcm_reg_token;


    @SerializedName("login_status")
    private String login_status;

    @SerializedName("email")
    private String email;


    @SerializedName("phone")
    private String phone;

    @SerializedName("customer_rating")
    private String customer_rating;

    @SerializedName("image")
    private String image;

    public String getLogin_status() {
        return login_status;
    }

    public void setLogin_status(String login_status) {
        this.login_status = login_status;
    }









    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }
    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }
    public String getFcm_reg_token() {
        return fcm_reg_token;
    }

    public void setFcm_reg_token(String fcm_reg_token) {
        this.fcm_reg_token = fcm_reg_token;
    }
    public String getDevice_type() {
        return device_type;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCustomer_rating() {
        return customer_rating;
    }

    public void setCustomer_rating(String customer_rating) {
        this.customer_rating = customer_rating;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



}
