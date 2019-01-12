package com.project.sketch.ugo.httpRequest.apiModel4;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Developer on 2/19/18.
 */

public class FeedbackRatingResponse {

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
