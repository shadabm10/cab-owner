package com.project.sketch.ugo.httpRequest.apiModel2;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Developer on 1/30/18.
 */

public class ConfirmBookResponce {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;


    @SerializedName("booking_Array")
    private Driver driver;


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

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
