package com.project.sketch.ugo.httpRequest.apiModel9;

import com.google.gson.annotations.SerializedName;
import com.project.sketch.ugo.httpRequest.apiModel2.Driver;

/**
 * Created by Developer on 1/30/18.
 */

public class OngoingBooking {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;


    @SerializedName("booking_info")
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
