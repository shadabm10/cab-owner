package com.project.sketch.ugo.httpRequest.apiModel3;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Developer on 2/1/18.
 */

public class BookingHistoryAll {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("booking_Details")
    private List<BookHisSingle> booking_Details;

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

    public List<BookHisSingle> getBooking_Details() {
        return booking_Details;
    }

    public void setBooking_Details(List<BookHisSingle> booking_Details) {
        this.booking_Details = booking_Details;
    }
}
