package com.project.sketch.ugo.httpRequest.apiModel5;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developer on 6/3/18.
 */

public class RentalRate {

    @SerializedName("id")
    private String id;

    @SerializedName("trip_distance")
    private float trip_distance;

    @SerializedName("distance_unit")
    private String distance_unit;


    @SerializedName("trip_duration")
    private float trip_duration;

    @SerializedName("duration_unit")
    private String duration_unit;


    @SerializedName("estimated_fare")
    private float estimated_fare;

    @SerializedName("guide_charge")
    private float guide_charge;




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getTrip_distance() {
        return trip_distance;
    }

    public void setTrip_distance(float trip_distance) {
        this.trip_distance = trip_distance;
    }

    public String getDistance_unit() {
        return distance_unit;
    }

    public void setDistance_unit(String distance_unit) {
        this.distance_unit = distance_unit;
    }

    public float getTrip_duration() {
        return trip_duration;
    }

    public void setTrip_duration(float trip_duration) {
        this.trip_duration = trip_duration;
    }

    public String getDuration_unit() {
        return duration_unit;
    }

    public void setDuration_unit(String duration_unit) {
        this.duration_unit = duration_unit;
    }

    public float getEstimated_fare() {
        return estimated_fare;
    }

    public void setEstimated_fare(float estimated_fare) {
        this.estimated_fare = estimated_fare;
    }

    public float getGuide_charge() {
        return guide_charge;
    }

    public void setGuide_charge(float guide_charge) {
        this.guide_charge = guide_charge;
    }
}
