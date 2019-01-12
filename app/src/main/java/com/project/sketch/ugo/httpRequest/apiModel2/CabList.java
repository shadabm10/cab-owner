package com.project.sketch.ugo.httpRequest.apiModel2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Developer on 1/29/18.
 */

public class CabList {



    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("missed_call_number")
    private String missed_call_number;

    @SerializedName("cab_Details_City_Taxi")
    private List<Cab> cab_Details_City_Taxi;


    @SerializedName("cab_Details_OutStation")
    private List<Cab> cab_Details_OutStation;

    @SerializedName("cab_Details_Rental")
    private List<Cab> cab_Details_Rental;



    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMissed_call_number() {
        return missed_call_number;
    }

    public void setMissed_call_number(String missed_call_number) {
        this.missed_call_number = missed_call_number;
    }

    public List<Cab> getCab_Details_City_Taxi() {
        return cab_Details_City_Taxi;
    }

    public void setCab_Details_City_Taxi(List<Cab> cab_Details_City_Taxi) {
        this.cab_Details_City_Taxi = cab_Details_City_Taxi;
    }

    public List<Cab> getCab_Details_OutStation() {
        return cab_Details_OutStation;
    }

    public void setCab_Details_OutStation(List<Cab> cab_Details_OutStation) {
        this.cab_Details_OutStation = cab_Details_OutStation;
    }

    public List<Cab> getCab_Details_Rental() {
        return cab_Details_Rental;
    }

    public void setCab_Details_Rental(List<Cab> cab_Details_Rental) {
        this.cab_Details_Rental = cab_Details_Rental;
    }
}
