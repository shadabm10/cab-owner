package com.project.sketch.ugo.screen;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.adapter.DialogSpecialFareListAdapter;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel7.SpecialFareData;
import com.project.sketch.ugo.httpRequest.apiModel7.SpecialFareResponse;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.GlobalClass;
import com.project.sketch.ugo.utils.SharedPref;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by developer on 24/3/18.
 */

public class SpecialFareDialog extends Dialog {

    private Context context;
    Button btn_citytaxi, btn_outstation;
    ListView lv_fare;
    ImageView iv_dismiss;
    ProgressDialog pDialog;
    SharedPref sharedPref;
    GlobalClass globalClass;

    DialogSpecialFareListAdapter specialFareListAdapter;

    public SpecialFareDialog(@NonNull Context context_) {
        super(context_);
        this.context = context_;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.specialfare_dialog);
        //setCancelable(false);

        pDialog = new ProgressDialog(context);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading fare chart...");

        sharedPref = new SharedPref(context);
        globalClass = (GlobalClass) context.getApplicationContext();



        btn_citytaxi = (Button) findViewById(R.id.btn_citytaxi);
        btn_outstation = (Button) findViewById(R.id.btn_outstation);
        lv_fare = (ListView) findViewById(R.id.lv_fare);
        iv_dismiss = (ImageView) findViewById(R.id.iv_dismiss);


        if (globalClass.getSpecialFareDataCityTaxi() == null){

            getSplFareCityTaxi();

        }else {

            specialFareListAdapter = new DialogSpecialFareListAdapter(context,
                    globalClass.getSpecialFareDataCityTaxi());
            lv_fare.setAdapter(specialFareListAdapter);

        }


        actionViews();


    }

    public void actionViews(){

        btn_citytaxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_citytaxi.setBackgroundColor(context.getResources().getColor(R.color.app_button_black1));
                btn_citytaxi.setTextColor(context.getResources().getColor(R.color.white));

                btn_outstation.setBackgroundColor(context.getResources().getColor(R.color.white));
                btn_outstation.setTextColor(context.getResources().getColor(R.color.app_button_black1));



                if (globalClass.getSpecialFareDataCityTaxi() == null){

                    getSplFareCityTaxi();

                }else {

                    specialFareListAdapter = new DialogSpecialFareListAdapter(context,
                            globalClass.getSpecialFareDataCityTaxi());
                    lv_fare.setAdapter(specialFareListAdapter);
                    specialFareListAdapter.notifyDataSetChanged();

                }

            }
        });


        btn_outstation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_outstation.setBackgroundColor(context.getResources().getColor(R.color.app_button_black1));
                btn_outstation.setTextColor(context.getResources().getColor(R.color.white));

                btn_citytaxi.setBackgroundColor(context.getResources().getColor(R.color.white));
                btn_citytaxi.setTextColor(context.getResources().getColor(R.color.app_button_black1));


                if (globalClass.getSpecialFareDataOutstation() == null){

                    getSplFareOutstation();

                }else {

                    specialFareListAdapter = new DialogSpecialFareListAdapter(context,
                            globalClass.getSpecialFareDataOutstation());
                    lv_fare.setAdapter(specialFareListAdapter);
                    specialFareListAdapter.notifyDataSetChanged();

                }

            }
        });



        iv_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


    @Override
    public void dismiss() {
        super.dismiss();

    }

/*
    public void dismissDialog(){

        if (isShowing()){
            dismiss();
        }
    }
*/


    private void getSplFareCityTaxi(){
        pDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<SpecialFareResponse> call =
                apiService.specialfarelocationcitytaxi(sharedPref.getUserId());

        call.enqueue(new Callback<SpecialFareResponse>() {
            @Override
            public void onResponse(Call<SpecialFareResponse>call,
                                   retrofit2.Response<SpecialFareResponse> response) {

                try{
                    if (response != null){


                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            List<SpecialFareData> specialFareData = response.body().getFare_info();

                            globalClass.setSpecialFareDataCityTaxi(specialFareData);

                            specialFareListAdapter = new DialogSpecialFareListAdapter(context,
                                    specialFareData);
                            lv_fare.setAdapter(specialFareListAdapter);
                            specialFareListAdapter.notifyDataSetChanged();

                        }



                    }

                }catch (Exception e){
                    e.printStackTrace();
                }



                pDialog.dismiss();

            }

            @Override
            public void onFailure(Call<SpecialFareResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

                pDialog.dismiss();

            }
        });


    }


    private void getSplFareOutstation(){

        pDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<SpecialFareResponse> call =
                apiService.specialfarelocationoutstation(sharedPref.getUserId());

        call.enqueue(new Callback<SpecialFareResponse>() {
            @Override
            public void onResponse(Call<SpecialFareResponse>call,
                                   retrofit2.Response<SpecialFareResponse> response) {


                try{
                    if (response != null){


                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            List<SpecialFareData> specialFareData = response.body().getFare_info();

                            globalClass.setSpecialFareDataOutstation(specialFareData);

                            specialFareListAdapter = new DialogSpecialFareListAdapter(context,
                                    specialFareData);
                            lv_fare.setAdapter(specialFareListAdapter);
                            specialFareListAdapter.notifyDataSetChanged();


                        }



                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


                pDialog.dismiss();

            }

            @Override
            public void onFailure(Call<SpecialFareResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

                pDialog.dismiss();

            }
        });


    }


}
