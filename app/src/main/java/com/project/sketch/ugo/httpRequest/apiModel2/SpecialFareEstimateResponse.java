package com.project.sketch.ugo.httpRequest.apiModel2;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developer on 26/3/18.
 */

public class SpecialFareEstimateResponse {


    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("specialFareType")
    private String specialFareType;

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

    public String getSpecialFareType() {
        return specialFareType;
    }

    public void setSpecialFareType(String specialFareType) {
        this.specialFareType = specialFareType;
    }

    public EstimateData getEstimateData() {
        return estimateData;
    }

    public void setEstimateData(EstimateData estimateData) {
        this.estimateData = estimateData;
    }
}
