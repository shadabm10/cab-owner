package com.project.sketch.ugo.httpRequest.apiModel7;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by developer on 25/3/18.
 */

public class SpecialFareResponse {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("fare_info")
    private List<SpecialFareData> fare_info;


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

    public List<SpecialFareData> getFare_info() {
        return fare_info;
    }

    public void setFare_info(List<SpecialFareData> fare_info) {
        this.fare_info = fare_info;
    }
}
