package com.project.sketch.ugo.httpRequest.apiModel7;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by developer on 28/3/18.
 */

public class CabDetailsResponse {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("cab_info")
    private List<CabData> cab_info;


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

    public List<CabData> getCab_info() {
        return cab_info;
    }

    public void setCab_info(List<CabData> cab_info) {
        this.cab_info = cab_info;
    }
}
