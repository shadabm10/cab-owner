package com.project.sketch.ugo.httpRequest.apiModel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Developer on 1/24/18.
 */

public class ForgotPassResponce {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("OTP")
    private String OTP;


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

    public String getOTP() {
        return OTP;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }
}
