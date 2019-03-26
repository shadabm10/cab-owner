package com.project.sketch.ugo.httpRequest.apiModel3;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Developer on 2/1/18.
 */

public class BookHisSingle implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("uid")
    private String uid;

    @SerializedName("did")
    private String did;

    @SerializedName("invoice_number")
    private String invoice_number;

    @SerializedName("pick_address")
    private String pick_address;

    @SerializedName("pick_lat")
    private String pick_lat;

    @SerializedName("pick_lng")
    private String pick_lng;

    @SerializedName("drop_address")
    private String drop_address;

    @SerializedName("drop_lat")
    private String drop_lat;

    @SerializedName("drop_lng")
    private String drop_lng;

    @SerializedName("cancel_by")
    private String cancel_by;

    @SerializedName("cancel_reason")
    private String cancel_reason;

    @SerializedName("driver_alloted_time")
    private String driver_alloted_time;

    @SerializedName("booking_status")
    private String booking_status;

    @SerializedName("booking_km")
    private String booking_km;

    @SerializedName("booking_total_time")
    private String booking_total_time;

    @SerializedName("created_date")
    private String created_date;

    @SerializedName("trip_start_time")
    private String trip_start_time;

    @SerializedName("trip_end_time")
    private String trip_end_time;


    @SerializedName("total_fare")
    private String total_fare;

    @SerializedName("guide_charges")
    private String guide_charges;

    @SerializedName("commission_rate")
    private String commission_rate;

    @SerializedName("gst_rate")
    private String gst_rate;

    @SerializedName("guide")
    private String guide;



    @SerializedName("gst_amt")
    private String gst_amt;


    public String getWaiting_price() {
        return waiting_price;
    }

    public void setWaiting_price(String waiting_price) {
        this.waiting_price = waiting_price;
    }

    @SerializedName("waiting_price")
    private String waiting_price;


    public String getCommission_amt() {
        return commission_amt;
    }

    public void setCommission_amt(String commission_amt) {
        this.commission_amt = commission_amt;
    }

    @SerializedName("commission_amt")
    private String commission_amt;

    public String getSub_total_amt() {
        return sub_total_amt;
    }

    public void setSub_total_amt(String sub_total_amt) {
        this.sub_total_amt = sub_total_amt;
    }

    @SerializedName("sub_total_amt")
    private String sub_total_amt;


    @SerializedName("coupon_applied")
    private String coupon_applied;

    @SerializedName("coupon_code")
    private String coupon_code;

    @SerializedName("coupon_amount")
    private float coupon_amount;


    @SerializedName("phone")
    private String driver_phone;

    @SerializedName("name")
    private String driver_name;

    @SerializedName("car_name")
    private String car_name;

    @SerializedName("number")
    private String car_number;

    @SerializedName("cab_name")
    private String cab_name;

    @SerializedName("car_image")
    private String car_image;

    @SerializedName("driver_image")
    private String driver_image;

    @SerializedName("payment_mode")
    private String payment_mode;

    @SerializedName("rate")
    private String driver_rating_by_me;






    public String getGst_amt() {
        return gst_amt;
    }

    public void setGst_amt(String gst_amt) {
        this.gst_amt = gst_amt;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public String getPick_address() {
        return pick_address;
    }

    public void setPick_address(String pick_address) {
        this.pick_address = pick_address;
    }

    public String getPick_lat() {
        return pick_lat;
    }

    public void setPick_lat(String pick_lat) {
        this.pick_lat = pick_lat;
    }

    public String getPick_lng() {
        return pick_lng;
    }

    public void setPick_lng(String pick_lng) {
        this.pick_lng = pick_lng;
    }

    public String getDrop_address() {
        return drop_address;
    }

    public void setDrop_address(String drop_address) {
        this.drop_address = drop_address;
    }

    public String getDrop_lat() {
        return drop_lat;
    }

    public void setDrop_lat(String drop_lat) {
        this.drop_lat = drop_lat;
    }

    public String getDrop_lng() {
        return drop_lng;
    }

    public void setDrop_lng(String drop_lng) {
        this.drop_lng = drop_lng;
    }

    public String getCancel_by() {
        return cancel_by;
    }

    public void setCancel_by(String cancel_by) {
        this.cancel_by = cancel_by;
    }

    public String getCancel_reason() {
        return cancel_reason;
    }

    public void setCancel_reason(String cancel_reason) {
        this.cancel_reason = cancel_reason;
    }

    public String getDriver_alloted_time() {
        return driver_alloted_time;
    }

    public void setDriver_alloted_time(String driver_alloted_time) {
        this.driver_alloted_time = driver_alloted_time;
    }

    public String getBooking_status() {
        return booking_status;
    }

    public void setBooking_status(String booking_status) {
        this.booking_status = booking_status;
    }

    public String getBooking_km() {
        return booking_km;
    }

    public void setBooking_km(String booking_km) {
        this.booking_km = booking_km;
    }

    public String getBooking_total_time() {
        return booking_total_time;
    }

    public void setBooking_total_time(String booking_total_time) {
        this.booking_total_time = booking_total_time;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getTrip_start_time() {
        return trip_start_time;
    }

    public void setTrip_start_time(String trip_start_time) {
        this.trip_start_time = trip_start_time;
    }

    public String getTrip_end_time() {
        return trip_end_time;
    }

    public void setTrip_end_time(String trip_end_time) {
        this.trip_end_time = trip_end_time;
    }

    public String getDriver_phone() {
        return driver_phone;
    }

    public void setDriver_phone(String driver_phone) {
        this.driver_phone = driver_phone;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
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

    public String getCab_name() {
        return cab_name;
    }

    public void setCab_name(String cab_name) {
        this.cab_name = cab_name;
    }

    public String getCar_image() {
        return car_image;
    }

    public void setCar_image(String car_image) {
        this.car_image = car_image;
    }


    public String getDriver_image() {
        return driver_image;
    }

    public void setDriver_image(String driver_image) {
        this.driver_image = driver_image;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getDriver_rating_by_me() {
        return driver_rating_by_me;
    }

    public void setDriver_rating_by_me(String driver_rating_by_me) {
        this.driver_rating_by_me = driver_rating_by_me;
    }


    public String getTotal_fare() {
        return total_fare;
    }

    public void setTotal_fare(String total_fare) {
        this.total_fare = total_fare;
    }

    public String getGuide_charges() {
        return guide_charges;
    }

    public void setGuide_charges(String guide_charges) {
        this.guide_charges = guide_charges;
    }

    public String getCommission_rate() {
        return commission_rate;
    }

    public void setCommission_rate(String commission_rate) {
        this.commission_rate = commission_rate;
    }

    public String getGst_rate() {
        return gst_rate;
    }

    public void setGst_rate(String gst_rate) {
        this.gst_rate = gst_rate;
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }


    public String getCoupon_applied() {
        return coupon_applied;
    }

    public void setCoupon_applied(String coupon_applied) {
        this.coupon_applied = coupon_applied;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public float getCoupon_amount() {
        return coupon_amount;
    }

    public void setCoupon_amount(float coupon_amount) {
        this.coupon_amount = coupon_amount;
    }
}
