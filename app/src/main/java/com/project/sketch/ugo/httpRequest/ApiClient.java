package com.project.sketch.ugo.httpRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Developer on 1/22/18.
 */

public class ApiClient {


   // public static final String BASE_URL = "https://www.gaincabs.com/";
 //   public static final String BASE_URL = "http://lab-3.sketchdemos.com/P1163_UGO/";
   // public static final String BASE_URL = "http://apps.mars-west.sws.sketchwebsolutions.net/P-1163-UGO/";
    public static final String BASE_URL = "http://u-go.in/";
   // public static final String BASE_URL = "http://lab-5.sketchdemos.com/PHP-WEB-SERVICES/P-1048-GainCabs/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }





}
