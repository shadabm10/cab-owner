package com.project.sketch.ugo.httpRequest.apiModel6;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developer on 24/3/18.
 */

public class CouponModel {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("promocode_info")
    private String promocode_info;

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

    public String getPromocode_info() {
        return promocode_info;
    }

    public void setPromocode_info(String promocode_info) {
        this.promocode_info = promocode_info;
    }
}
