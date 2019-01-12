package com.project.sketch.ugo.datamodel;

/**
 * Created by ANDROID on 1/10/2018.
 */

public class RideModel {

    private String ride_id_, pickup_address_ ,drop_address_ , status_, date_;
    private int driver_img_;

    public RideModel(String ride_id,String pickup_address, String drop_address, String status, int driver_img, String date){
        this.ride_id_=ride_id;
        this.pickup_address_=pickup_address;
        this.drop_address_=drop_address;
        this.status_=status;
        this.driver_img_ = driver_img;
        this.date_ = date;
    }

    public String getRide_id_() {
        return ride_id_;
    }

    public String getPickup_address_() {
        return pickup_address_;
    }

    public String getDrop_address_() {
        return drop_address_;
    }

    public String getStatus_() {
        return status_;
    }

    public String getDate_() {
        return date_;
    }

    public int getDriver_img_() {
        return driver_img_;
    }
}
