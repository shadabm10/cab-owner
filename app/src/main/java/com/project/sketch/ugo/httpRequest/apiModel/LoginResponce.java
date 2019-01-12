package com.project.sketch.ugo.httpRequest.apiModel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Developer on 1/23/18.
 */

public class LoginResponce {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("customer_info")
    private CustomerInfo customer_info;

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

    public CustomerInfo getCustomerInfo() {
        return customer_info;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customer_info = customerInfo;
    }
}
