package com.project.sketch.ugo.database;

/**
 * Created by Developer on 2/1/18.
 */

public class FavDataModel {

    private String ID;
    private String TYPE;
    private String TITLE;
    private String ADDRESS;
    private String LATITUDE;
    private String LONGITUDE;


    public FavDataModel() {
    }


    public FavDataModel(String ID, String TYPE, String TITLE, String ADDRESS, String LATITUDE, String LONGITUDE) {
        this.ID = ID;
        this.TYPE = TYPE;
        this.TITLE = TITLE;
        this.ADDRESS = ADDRESS;
        this.LATITUDE = LATITUDE;
        this.LONGITUDE = LONGITUDE;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getLATITUDE() {
        return LATITUDE;
    }

    public void setLATITUDE(String LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public String getLONGITUDE() {
        return LONGITUDE;
    }

    public void setLONGITUDE(String LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }
}
