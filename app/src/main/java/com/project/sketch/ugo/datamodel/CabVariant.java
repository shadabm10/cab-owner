package com.project.sketch.ugo.datamodel;

/**
 * Created by Developer on 1/19/18.
 */

public class CabVariant {


    private String cab_id;
    private String cab_name;
    private String selectImage;
    private String unselectImage;
    private String cab_reaching_time;
    private int noOfcab;

    public CabVariant() {
    }

    public String getCab_id() {
        return cab_id;
    }

    public void setCab_id(String cab_id) {
        this.cab_id = cab_id;
    }

    public String getCab_name() {
        return cab_name;
    }

    public void setCab_name(String cab_name) {
        this.cab_name = cab_name;
    }

    public String getSelectImage() {
        return selectImage;
    }

    public void setSelectImage(String selectImage) {
        this.selectImage = selectImage;
    }

    public String getUnselectImage() {
        return unselectImage;
    }

    public void setUnselectImage(String unselectImage) {
        this.unselectImage = unselectImage;
    }

    public String getCab_reaching_time() {
        return cab_reaching_time;
    }

    public void setCab_reaching_time(String cab_reaching_time) {
        this.cab_reaching_time = cab_reaching_time;
    }

    public int getNoOfcab() {
        return noOfcab;
    }

    public void setNoOfcab(int noOfcab) {
        this.noOfcab = noOfcab;
    }



}
