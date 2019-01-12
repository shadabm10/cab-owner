package com.project.sketch.ugo.httpRequest.apiModel5;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by developer on 6/3/18.
 */

public class RentalRateList {


    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("rental_info")
    private List<RentalRate> rental_info;


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

    public List<RentalRate> getRental_info() {
        return rental_info;
    }

    public void setRental_info(List<RentalRate> rental_info) {
        this.rental_info = rental_info;
    }


}
