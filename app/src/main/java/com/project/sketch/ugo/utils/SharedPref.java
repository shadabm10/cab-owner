package com.project.sketch.ugo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.project.sketch.ugo.httpRequest.apiModel2.Driver;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Developer on 1/23/18.
 */

public class SharedPref {

    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private SharedPreferences pref_ride;
    private SharedPreferences.Editor editor_ride;


    private static final String MainKey = "GainPref";
    private static final String MainKey_ride = "GainPref_ride";

    private static final String key_id = "customer_id";
    private static final String key_name = "name_";
    private static final String key_email = "email_";
    private static final String key_phone = "phone_";
    private static final String key_iamge = "image_";
    private static final String key_FirstLogin = "First_Login";
    private static final String Paymentmode = "Paymentmode";




    private static final String Key_device_type = "device_type";


    private static final String Key_fcm_reg_token = "fcm_reg_token";



    private static final String Key_deviceid = "login_status";
    private static final String Key_login_status = "login_status";

    private static final String RideComplete = "RideComplete";
    private static final String missedcall_number = "missedcall_number";
    private static final String is_riding = "is_riding";
    private static final String bidd = "bidd";
    private static final String driver_id = "driver_id";
    private static final String pickup_addr = "pickup_addr";
    private static final String drop_addr = "drop_addr";
    private static final String driver_data = "driver_data";
    private static final String key_customer_rating = "customer_rating";

    private static final String isFreeForBooking = "isFreeForBooking";


    public SharedPref(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(MainKey, MODE_PRIVATE);
        editor = pref.edit();

        pref_ride = context.getSharedPreferences(MainKey_ride, MODE_PRIVATE);
        editor_ride = pref_ride.edit();

    }

    public static String getKey_device_type() {
        return Key_device_type;
    }
    public static String getKey_fcm_reg_token() {
        return Key_fcm_reg_token;
    }
    public static String getKey_deviceid() {
        return Key_deviceid;
    }
    public void setFirstLogin(boolean boo){
        editor.putBoolean(key_FirstLogin, boo);
        editor.commit();
    }

    public boolean idFirstLogin(){
        return pref.getBoolean(key_FirstLogin, false);
    }



    public void setUserId(String id){
        editor.putString(key_id, id);
        editor.commit();

    }

    public String getUserId(){
       return pref.getString(key_id, "");
    }


    public void setUserInfo(String name, String email, String phone,
                            String image,String rating,String login_status,
                            String fcm_reg_token,String deviceid,String device_type){
        editor.putString(key_name, name);
        editor.putString(key_email, email);
        editor.putString(key_phone, phone);
        editor.putString(key_iamge, image);
        editor.putString(key_customer_rating,rating);
        editor.putString(Key_login_status,login_status);
        editor.putString(Key_fcm_reg_token,fcm_reg_token);
        editor.putString(Key_deviceid,deviceid);
        editor.putString(Key_device_type,device_type);
        editor.commit();

    }
    public  String getKey_login_status() {
        return   pref.getString(Key_login_status, "");

    }
    public String getUserName(){
        return pref.getString(key_name, "");
    }

    public String getUserEmail(){
        return pref.getString(key_email, "");
    }

    public String getUserPhone(){
        return pref.getString(key_phone, "");
    }

    public String getUserImage(){
        return pref.getString(key_iamge, "");
    }
    public String getCustomer_rating(){
        return pref.getString(key_customer_rating, "");
    }



    public void setPaymentMode(String mode){
        editor.putString(Paymentmode, mode);
        editor.commit();

    }


    public String getPaymentMode(){
       return pref.getString(Paymentmode, Constants.paymentMode_Wallet);
    }


    public void saveMissedCallNumber(String number){
        editor.putString(missedcall_number, number);
        editor.commit();
    }


    public String getMissedCall_number(){
        return pref.getString(missedcall_number, "");
    }


    public void resetData(){
        editor.clear();
        editor.commit();
    }


    public void setFreeForBooking(boolean bool){
        editor.putBoolean(isFreeForBooking, bool);
        editor.commit();
    }

    public boolean isFreeForBooking(){
        return pref.getBoolean(isFreeForBooking, true);
    }

    //////////////////////////////////////////////

    //////////////////////////////////////////////////////////////

    ///// ride details .....

   /* public void setNowRide(boolean boo){
        editor.putBoolean(NowRide, boo);
        editor.commit();
    }

    public boolean isNowRide(){
        return pref.getBoolean(NowRide, false);
    }*/


    public void saveRiding(boolean val){
        editor_ride.putBoolean(is_riding, val);
        editor_ride.commit();
    }

    public boolean isRiding(){
        return pref_ride.getBoolean(is_riding, false);
    }


    public void setRideComplete(boolean boo){
        editor_ride.putBoolean(RideComplete, boo);
        editor_ride.commit();
    }

    public boolean isRideComplete(){
        return pref_ride.getBoolean(RideComplete, false);
    }


    public void saveBookingId(String b_id){
        editor_ride.putString(bidd, b_id);
        editor_ride.commit();
    }

    public String getBookingId(){
       return pref_ride.getString(bidd, "");
    }


    public void saveDriver_id(String did){
        editor_ride.putString(driver_id, did);
        editor_ride.commit();
    }

    public String getDriver_id() {
        return pref_ride.getString(driver_id, "");
    }


    public void savePickup_addr(String pup_addr){
        editor_ride.putString(pickup_addr, pup_addr);
        editor_ride.commit();
    }

    public String getPickup_addr() {
        return pref_ride.getString(pickup_addr, "");
    }


    public void saveDrop_addr(String dro_addr){
        editor_ride.putString(drop_addr, dro_addr);
        editor_ride.commit();

    }

    public String getDrop_addr() {
        return pref_ride.getString(drop_addr, "");
    }

    private static final String IsDriverAccept = "IsDriverAccept";
    public void saveIsDriverAccept(boolean boo){
        editor_ride.putBoolean(IsDriverAccept, boo);
        editor_ride.commit();
    }

    public boolean isDriverAccept(){
        return pref_ride.getBoolean(IsDriverAccept, false);
    }


    private static final String IsReachingThePickupLocation = "IsReachingThePickupLocation";
    public void saveDriverIsReachingThePickupLocation(boolean boo){
        editor_ride.putBoolean(IsReachingThePickupLocation, boo);
        editor_ride.commit();
    }

    public boolean isReachingThePickupLocation(){
        return pref_ride.getBoolean(IsReachingThePickupLocation, false);
    }



    public void saveDriverInfo(Driver driver){
        Gson gson = new Gson();
        String json = gson.toJson(driver);

        editor_ride.putString(driver_data, json);
        editor_ride.commit();
    }

    public Driver getDriverData(){
        Gson gson = new Gson();
        String json = pref_ride.getString(driver_data, "");
        Driver obj = gson.fromJson(json, Driver.class);
        return obj;
    }


    public void resetRideData(){
        editor_ride.clear();
        editor_ride.commit();
    }

/////////////////////////////////////////////////////////////////////////////






}
