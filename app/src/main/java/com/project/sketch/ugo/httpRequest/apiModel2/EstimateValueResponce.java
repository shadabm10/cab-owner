package com.project.sketch.ugo.httpRequest.apiModel2;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Developer on 2/7/18.
 */

public class EstimateValueResponce {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("estimateArr")
    private EstimateData estimateData;



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

    public EstimateData getEstimateData() {
        return estimateData;
    }

    public void setEstimateData(EstimateData estimateData) {
        this.estimateData = estimateData;
    }
}
