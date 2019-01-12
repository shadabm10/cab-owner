package com.project.sketch.ugo.httpRequest.apiModel2;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Developer on 2/7/18.
 */

public class OutstationEstimateResponse {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("estimateArr")
    private OutstationEstimateData estimateData;



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

    public OutstationEstimateData getEstimateData() {
        return estimateData;
    }

    public void setEstimateData(OutstationEstimateData estimateData) {
        this.estimateData = estimateData;
    }
}
