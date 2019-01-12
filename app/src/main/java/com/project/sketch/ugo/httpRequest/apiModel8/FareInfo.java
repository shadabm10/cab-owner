package com.project.sketch.ugo.httpRequest.apiModel8;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developer on 4/5/18.
 */

public class FareInfo {

    @SerializedName("total_fare")
    private double total_fare;

    @SerializedName("guide_charges")
    private double guide_charges;

    @SerializedName("commission_rate")
    private double commission_rate;

    @SerializedName("gst_rate")
    private double gst_rate;

    @SerializedName("commission_rate_type")
    private String commission_rate_type;

    @SerializedName("gst_rate_type")
    private String gst_rate_type;


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
