package com.project.sketch.ugo.httpRequest.apiModel7;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by developer on 28/3/18.
 */

public class CabData {

    @SerializedName("name")
    private String name;

    @SerializedName("car")
    private List<String> car;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCar() {
        return car;
    }

    public void setCar(List<String> car) {
        this.car = car;
    }
}
