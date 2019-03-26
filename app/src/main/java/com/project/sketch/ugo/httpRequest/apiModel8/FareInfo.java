package com.project.sketch.ugo.httpRequest.apiModel8;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developer on 4/5/18.
 */

public class FareInfo {

    @SerializedName("total_fare")
    private double total_fare;

    @SerializedName("coupon_amount")
    private double coupon_amount;

    @SerializedName("sub_total_fare")
    private double sub_total_fare;

    public double getCoupon_amount() {
        return coupon_amount;
    }

    public void setCoupon_amount(double coupon_amount) {
        this.coupon_amount = coupon_amount;
    }



    @SerializedName("guide_charges")
    private double guide_charges;

    @SerializedName("commission_rate")
    private double commission_rate;

    @SerializedName("gst_fair")
    private double gst_fair;

    @SerializedName("commission_fair")
    private double commission_fair;

    @SerializedName("gst_rate")
    private double gst_rate;

    @SerializedName("commission_rate_type")
    private String commission_rate_type;

    @SerializedName("gst_rate_type")
    private String gst_rate_type;

    public String getWaiting_fair() {
        return waiting_fair;
    }

    public void setWaiting_fair(String waiting_fair) {
        this.waiting_fair = waiting_fair;
    }

    @SerializedName("waiting_fair")
    private String waiting_fair;

    public double getSub_total_fare() {
        return sub_total_fare;
    }

    public void setSub_total_fare(double sub_total_fare) {
        this.sub_total_fare = sub_total_fare;
    }



    public double getGst_fair() {
        return gst_fair;
    }

    public void setGst_fair(double gst_fair) {
        this.gst_fair = gst_fair;
    }


    public double getCommission_fair() {
        return commission_fair;
    }

    public void setCommission_fair(double commission_fair) {
        this.commission_fair = commission_fair;
    }




    public double getTotal_fare() {
        return total_fare;
    }

    public void setTotal_fare(double total_fare) {
        this.total_fare = total_fare;
    }

    public double getGuide_charges() {
        return guide_charges;
    }

    public void setGuide_charges(double guide_charges) {
        this.guide_charges = guide_charges;
    }

    public double getCommission_rate() {
        return commission_rate;
    }

    public void setCommission_rate(double commission_rate) {
        this.commission_rate = commission_rate;
    }

    public double getGst_rate() {
        return gst_rate;
    }

    public void setGst_rate(double gst_rate) {
        this.gst_rate = gst_rate;
    }

    public String getCommission_rate_type() {
        return commission_rate_type;
    }

    public void setCommission_rate_type(String commission_rate_type) {
        this.commission_rate_type = commission_rate_type;
    }

    public String getGst_rate_type() {
        return gst_rate_type;
    }

    public void setGst_rate_type(String gst_rate_type) {
        this.gst_rate_type = gst_rate_type;
    }



}
