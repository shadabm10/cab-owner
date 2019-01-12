package com.project.sketch.ugo.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.adapter.BookingHistoryAdapter;
import com.project.sketch.ugo.datamodel.RideModel;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel3.BookHisSingle;
import com.project.sketch.ugo.httpRequest.apiModel3.BookingHistoryAll;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Developer on 1/12/18.
 */

public class HistoryList extends Fragment {


    RecyclerView recyclerview ;
    ArrayList<RideModel> ride_model;
    LinearLayoutManager layoutManager;
    TextView tv_message;

    SharedPref sharedPref;
    ProgressDialog pDialog;
    BookingHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_booking_history, container, false);
        initViews(rootView);



        return rootView;
    }


    public void initViews(View view){

        sharedPref = new SharedPref(getActivity());

        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        ride_model = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity());

        tv_message = (TextView) view.findViewById(R.id.tv_message);
        tv_message.setVisibility(View.GONE);

        getBookingHistory();



    }



    private void getBookingHistory(){

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<BookingHistoryAll> call = apiService.getBookingHistory(sharedPref.getUserId());
       // Call<BookingHistoryAll> call = apiService.getBookingHistory("6");

        call.enqueue(new Callback<BookingHistoryAll>() {
            @Override
            public void onResponse(Call<BookingHistoryAll>call, retrofit2.Response<BookingHistoryAll> response) {

                try{

                    Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                    Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                    if (response.body().getStatus() == 1){

                        List<BookHisSingle> hisSingleList = response.body().getBooking_Details();

                        adapter = new BookingHistoryAdapter(getActivity(), hisSingleList);
                        recyclerview.setAdapter(adapter);
                        recyclerview.setLayoutManager(layoutManager);
                        recyclerview.addItemDecoration(new DividerItemDecoration(getActivity(), 0));


                    }else{

                        tv_message.setVisibility(View.VISIBLE);

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


                pDialog.dismiss();

            }

            @Override
            public void onFailure(Call<BookingHistoryAll> call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

                pDialog.dismiss();

            }
        });


    }


}
