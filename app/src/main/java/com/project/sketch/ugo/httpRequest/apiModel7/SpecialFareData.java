package com.project.sketch.ugo.httpRequest.apiModel7;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developer on 25/3/18.
 */

public class SpecialFareData {

    @SerializedName("id")
    private int id;

    @SerializedName("location_name_from")
    private String location_name_from;

    @SerializedName("location_name_to")
    private String location_name_to;

    @SerializedName("cab_name")
    private String cab_name;

    @SerializedName("total_fare")
    private String total_fare;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation_name_from() {
        return location_name_from;
    }

    public void setLocation_name_from(String location_name_from) {
        this.location_name_from = location_name_from;
    }

    public String getLocation_name_to() {
        return location_name_to;
    }

    public void setLocation_name_to(String location_name_to) {
        this.location_name_to = location_name_to;
    }

    public String getCab_name() {
        return cab_name;
    }

    public void setCab_name(String cab_name) {
        this.cab_name = cab_name;
    }

    public String getTotal_fare() {
        return total_fare;
    }

    public void setTotal_fare(String total_fare) {
        this.total_fare = total_fare;
    }
}
