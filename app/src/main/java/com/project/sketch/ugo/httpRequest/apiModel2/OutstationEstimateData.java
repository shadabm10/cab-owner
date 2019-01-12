package com.project.sketch.ugo.httpRequest.apiModel2;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Developer on 2/7/18.
 */

public class OutstationEstimateData implements Serializable {

    @SerializedName("estimate_distance_text")
    private String estimate_distance_text;

    @SerializedName("estimate_distance_value")
    private int estimate_distance_value;

    @SerializedName("estimate_fare_for_12_hour")
    private float estimate_fare_12_hour;

    @SerializedName("estimate_fare_for_12_hour_maximum")
    private float estimate_fare_12_hour_max;


    @SerializedName("estimate_fare_for_24_hour")
    private float estimate_fare_24_hour;

    @SerializedName("estimate_fare_for_24_hour_maximum")
    private float estimate_fare_24_hour_max;


    @SerializedName("estimate_time_text")
    private String estimate_time_text;

    @SerializedName("estimate_time_value")
    private int estimate_time_value;

    @SerializedName("estimate_guide_charge_for_12_hour")
    private float estimate_guide_charge_12_h;

    @SerializedName("estimate_guide_charge_for_24_hour")
    private float estimate_guide_charge_24_h;

    @SerializedName("total_fees_and_charges")
    private float total_fees_and_charges;


    public String getEstimate_distance_text() {
        return estimate_distance_text;
    }

    public void setEstimate_distance_text(String estimate_distance_text) {
        this.estimate_distance_text = estimate_distance_text;
    }

    public int getEstimate_distance_value() {
        return estimate_distance_value;
    }

    public void setEstimate_distance_value(int estimate_distance_value) {
        this.estimate_distance_value = estimate_distance_value;
    }

    public float getEstimate_fare_12_hour() {
        return estimate_fare_12_hour;
    }

    public void setEstimate_fare_12_hour(float estimate_fare_12_hour) {
        this.estimate_fare_12_hour = estimate_fare_12_hour;
    }

    public float getEstimate_fare_12_hour_max() {
        return estimate_fare_12_hour_max;
    }

    public void setEstimate_fare_12_hour_max(float estimate_fare_12_hour_max) {
        this.estimate_fare_12_hour_max = estimate_fare_12_hour_max;
    }

    public float getEstimate_fare_24_hour() {
        return estimate_fare_24_hour;
    }

    public void setEstimate_fare_24_hour(float estimate_fare_24_hour) {
        this.estimate_fare_24_hour = estimate_fare_24_hour;
    }

    public float getEstimate_fare_24_hour_max() {
        return estimate_fare_24_hour_max;
    }

    public void setEstimate_fare_24_hour_max(float estimate_fare_24_hour_max) {
        this.estimate_fare_24_hour_max = estimate_fare_24_hour_max;
    }

    public String getEstimate_time_text() {
        return estimate_time_text;
    }

    public void setEstimate_time_text(String estimate_time_text) {
        this.estimate_time_text = estimate_time_text;
    }

    public int getEstimate_time_value() {
        return estimate_time_value;
    }

    public void setEstimate_time_value(int estimate_time_value) {
        this.estimate_time_value = estimate_time_value;
    }

    public float getEstimate_guide_charge_12_h() {
        return estimate_guide_charge_12_h;
    }

    public void setEstimate_guide_charge_12_h(float estimate_guide_charge_12_h) {
        this.estimate_guide_charge_12_h = estimate_guide_charge_12_h;
    }

    public float getEstimate_guide_charge_24_h() {
        return estimate_guide_charge_24_h;
    }

    public void setEstimate_guide_charge_24_h(float estimate_guide_charge_24_h) {
        this.estimate_guide_charge_24_h = estimate_guide_charge_24_h;
    }

    public float getTotal_fees_and_charges() {
        return total_fees_and_charges;
    }

    public void setTotal_fees_and_charges(float total_fees_and_charges) {
        this.total_fees_and_charges = total_fees_and_charges;
    }
}
