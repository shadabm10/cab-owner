package com.project.sketch.ugo.httpRequest.apiModel2;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Developer on 2/6/18.
 */

public class DriverResponce {


    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("driver_Details")
    private Driver driver_Details;


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

    public Driver getDriver_Details() {
        return driver_Details;
    }

    public void setDriver_Details(Driver driver_Details) {
        this.driver_Details = driver_Details;
    }
}
