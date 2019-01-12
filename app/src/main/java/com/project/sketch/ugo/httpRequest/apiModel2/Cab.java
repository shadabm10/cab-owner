package com.project.sketch.ugo.httpRequest.apiModel2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Developer on 1/29/18.
 */

public class Cab {


    @SerializedName("cab_name")
    private String cab_name;

    @SerializedName("cab_id")
    private String cab_id;

    @SerializedName("select_image")
    private String select_image;

    @SerializedName("unselect_image")
    private String unselect_image;


    @SerializedName("driver_Array")
    private List<Driver> driver_Array;



    public String getCab_name() {
        return cab_name;
    }

    public void setCab_name(String cab_name) {
        this.cab_name = cab_name;
    }

    public String getCab_id() {
        return cab_id;
    }

    public void setCab_id(String cab_id) {
        this.cab_id = cab_id;
    }

    public List<Driver> getDriver_Array() {
        return driver_Array;
    }

    public void setDriver_Array(List<Driver> driver_Array) {
        this.driver_Array = driver_Array;
    }

    public String getSelect_image() {
        return select_image;
    }

    public void setSelect_image(String select_image) {
        this.select_image = select_image;
    }

    public String getUnselect_image() {
        return unselect_image;
    }

    public void setUnselect_image(String unselect_image) {
        this.unselect_image = unselect_image;
    }
}
