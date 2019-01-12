package com.project.sketch.ugo.utils;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.project.sketch.ugo.httpRequest.apiModel2.Driver;
import com.project.sketch.ugo.httpRequest.apiModel7.SpecialFareData;

import java.util.List;

import static com.project.sketch.ugo.utils.Constants.TAG;


/**
 * Created by Developer on 1/8/18.
 */

public class GlobalClass extends Application {

    public int DRAWER_FRAGMENT_ID;
    public String PICUP_ADDRESS = "";
    public String DROP_ADDRESS = "";
    public LatLng PICKUP_LATLNG, DROP_LATLNG;
    public String KEY_of_travel = "";
    public String Selected_Cab_Id = "";
    public String Selected_Cab_Name = "";
    public String Selected_Cab_Img = "";
    public boolean is_Cancel = false;
    public String Later_Ride = "";
    public String Booking_later_time = "";
    public String Pickup_City = "";
    public String DropOff_City = "";
    public boolean isSpecialFareSelect = false;
    public Bitmap cab_image=null;



    ///////////////////////////////////////



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////// Volley ......

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static GlobalClass mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized GlobalClass getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    private Driver driver;

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    /////////////////////////////

    private float coupon_amount = 0;
    private String coupon_code = "";
    private String coupon_applied = "N";

    public float getCoupon_amount() {
        return coupon_amount;
    }

    public void setCoupon_amount(float coupon_amount) {
        this.coupon_amount = coupon_amount;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getCoupon_applied() {
        return coupon_applied;
    }

    public void setCoupon_applied(String coupon_applied) {
        this.coupon_applied = coupon_applied;
    }

    ///////////////////////////

    ///// Special fare data

    private List<SpecialFareData> specialFareDataCityTaxi;
    private List<SpecialFareData> specialFareDataOutstation;

    public List<SpecialFareData> getSpecialFareDataCityTaxi() {
        return specialFareDataCityTaxi;
    }

    public void setSpecialFareDataCityTaxi(List<SpecialFareData> specialFareDataCityTaxi) {
        this.specialFareDataCityTaxi = specialFareDataCityTaxi;
    }

    public List<SpecialFareData> getSpecialFareDataOutstation() {
        return specialFareDataOutstation;
    }

    public void setSpecialFareDataOutstation(List<SpecialFareData> specialFareDataOutstation) {
        this.specialFareDataOutstation = specialFareDataOutstation;
    }

    ////////////////////////////

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }




}
