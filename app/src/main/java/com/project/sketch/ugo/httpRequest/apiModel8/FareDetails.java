package com.project.sketch.ugo.httpRequest.apiModel8;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developer on 28/3/18.
 */

public class FareDetails {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("fare_info")
    private FareInfo fareInfo;


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


    public FareInfo getFareInfo() {
        return fareInfo;
    }

    public void setFareInfo(FareInfo fareInfo) {
        this.fareInfo = fareInfo;
    }
}
