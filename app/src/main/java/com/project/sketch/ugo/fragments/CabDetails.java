package com.project.sketch.ugo.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.adapter.CabDetailsAdapter;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel7.CabData;
import com.project.sketch.ugo.httpRequest.apiModel7.CabDetailsResponse;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.SharedPref;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by developer on 28/3/18.
 */

public class CabDetails extends Fragment {


    ListView lv_cab_list;
    ProgressDialog pDialog;
    SharedPref sharedPref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_cab_details, container, false);
        initViews(rootView);



        return rootView;
    }

    public void initViews(View view){

        lv_cab_list = (ListView) view.findViewById(R.id.lv_cab_list);

        sharedPref = new SharedPref(getActivity());



        getCabDetails();

    }





    private void getCabDetails(){

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CabDetailsResponse> call = apiService.get_cab_details(sharedPref.getUserId());

        call.enqueue(new Callback<CabDetailsResponse>() {
            @Override
            public void onResponse(Call<CabDetailsResponse>call, retrofit2.Response<CabDetailsResponse> response) {

                try{
                    if (response != null){



                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            List<CabData> cabDataList = response.body().getCab_info();

                            CabDetailsAdapter cabDetailsAdapter = new CabDetailsAdapter(getActivity(),
                                    cabDataList);
                            lv_cab_list.setAdapter(cabDetailsAdapter);


                        }else{



                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }



                pDialog.dismiss();

            }

            @Override
            public void onFailure(Call<CabDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

                pDialog.dismiss();

            }
        });


    }




}
