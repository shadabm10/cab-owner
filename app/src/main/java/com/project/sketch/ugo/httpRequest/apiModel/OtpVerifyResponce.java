package com.project.sketch.ugo.httpRequest.apiModel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Developer on 1/23/18.
 */

public class OtpVerifyResponce {

    @SerializedName("status")
    private int status;

    @SerializedName("id")
    private String id;

    @SerializedName("message")
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
