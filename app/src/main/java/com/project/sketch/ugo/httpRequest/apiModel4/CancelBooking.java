package com.project.sketch.ugo.httpRequest.apiModel4;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Developer on 2/15/18.
 */

public class CancelBooking {


    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;


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
}
