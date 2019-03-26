package com.project.sketch.ugo.httpRequest.apiModel2;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Developer on 1/24/18.
 */

public class Driver {

    @SerializedName("id")
    private String booking_id;

    @SerializedName("did")
    private String did;

    @SerializedName("lat")
    private String lat;

    @SerializedName("lng")
    private String lng;

    @SerializedName("phone")
    private String phone;

    @SerializedName("name")
    private String name;

    @SerializedName("booking_km")
    private String booking_km;

    @SerializedName("distance")
    private String distance;

    @SerializedName("car_name")
    private String car_name;

    @SerializedName("number")
    private String car_number;

    @SerializedName("car_image")
    private String car_image;

    @SerializedName("cab_id")
    private String cab_id;

    @SerializedName("cab_name")
    private String cab_name;

    @SerializedName("rating")
    private String rating;

    @SerializedName("driver_image")
    private String driver_image;

    @SerializedName("otp")
    private String otp;

    @SerializedName("booking_status")
    private String booking_status;

    @SerializedName("pick_address")
    private String pick_address;

    @SerializedName("drop_address")
    private String drop_address;


    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getBooking_km() {
        return booking_km;
    }

    public void setBooking_km(String booking_km) {
        this.booking_km = booking_km;
    }

    public String getCar_name() {
        return car_name;
    }

    public void setCar_name(String car_name) {
        this.car_name = car_name;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public String getCar_image() {
        return car_image;
    }

    public void setCar_image(String car_image) {
        this.car_image = car_image;
    }


    public String getCab_id() {
        return cab_id;
    }

    public void setCab_id(String cab_id) {
        this.cab_id = cab_id;
    }

    public String getCab_name() {
        return cab_name;
    }

    public void setCab_name(String cab_name) {
        this.cab_name = cab_name;
    }

    public String getDriver_image() {
        return driver_image;
    }

    public void setDriver_image(String driver_image) {
        this.driver_image = driver_image;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }


    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getBooking_status() {
        return booking_status;
    }

    public void setBooking_status(String booking_status) {
        this.booking_status = booking_status;
    }

    public String getPick_address() {
        return pick_address;
    }

    public void setPick_address(String pick_address) {
        this.pick_address = pick_address;
    }

    public String getDrop_address() {
        return drop_address;
    }

    public void setDrop_address(String drop_address) {
        this.drop_address = drop_address;
    }
}
