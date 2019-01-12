package com.project.sketch.ugo.httpRequest.apiModel6;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developer on 2/4/18.
 */

public class ResendOtpResponse {


    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("otp")
    private String otp;

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

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
