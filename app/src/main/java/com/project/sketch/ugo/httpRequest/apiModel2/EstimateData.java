package com.project.sketch.ugo.httpRequest.apiModel2;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Developer on 2/7/18.
 */

public class EstimateData implements Serializable {

    @SerializedName("estimate_distance_text")
    private String estimate_distance_text;

    @SerializedName("estimate_distance_value")
    private int estimate_distance_value;

    @SerializedName("estimate_fare_maximum")
    private float estimate_fare_maximum;

    @SerializedName("estimate_fare")
    private float estimate_fare;

    @SerializedName("estimate_time_text")
    private String estimate_time_text;

    @SerializedName("estimate_time_value")
    private int estimate_time_value;

    @SerializedName("estimate_guide_charge")
    private float estimate_guide_charge;

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

    public float getEstimate_fare() {
        return estimate_fare;
    }

    public void setEstimate_fare(float estimate_fare) {
        this.estimate_fare = estimate_fare;
    }


    public float getEstimate_fare_maximum() {
        return estimate_fare_maximum;
    }

    public void setEstimate_fare_maximum(float estimate_fare_maximum) {
        this.estimate_fare_maximum = estimate_fare_maximum;
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

    public float getEstimate_guide_charge() {
        return estimate_guide_charge;
    }

    public void setEstimate_guide_charge(float estimate_guide_charge) {
        this.estimate_guide_charge = estimate_guide_charge;
    }

    public float getTotal_fees_and_charges() {
        return total_fees_and_charges;
    }

    public void setTotal_fees_and_charges(float total_fees_and_charges) {
        this.total_fees_and_charges = total_fees_and_charges;
    }
}
