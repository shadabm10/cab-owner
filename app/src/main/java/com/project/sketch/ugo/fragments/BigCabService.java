package com.project.sketch.ugo.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel4.CancelBooking;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.ValidationClass;

import retrofit2.Call;

/**
 * Created by developer on 5/3/18.
 */

public class BigCabService extends Fragment {

    TextView tv_2;
    EditText input_name, input_email, input_mobile, input_message;
    Button action_submit;

    ValidationClass validationClass;
    ProgressDialog pDialog;

    public FragmentTransaction ft;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bigcab_screen, container, false);


        InitViews(rootView);



        return rootView;
    }


    public void InitViews(View view){
        tv_2 = (TextView) view.findViewById(R.id.tv_2);
        input_name = (EditText) view.findViewById(R.id.input_name);
        input_email = (EditText) view.findViewById(R.id.input_email);
        input_mobile = (EditText) view.findViewById(R.id.input_mobile);
        input_message = (EditText) view.findViewById(R.id.input_message);
        action_submit = (Button) view.findViewById(R.id.action_submit);

        validationClass = new ValidationClass(getActivity());

        action_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkValidation();

            }
        });


    }


    public void checkValidation(){

        if (!validationClass.validateName(input_name))
            return;


        if (!validationClass.validateEmail(input_email))
            return;


        if (!validationClass.validateMobileNo(input_mobile))
            return;


        if (!validationClass.validateIsEmpty(input_message, "Enter message"))
            return;



        fillBigForm();

    }





    private void fillBigForm(){

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        pDialog.setMessage("Submitting...");
        pDialog.show();

        String name = input_name.getText().toString();
        String email = input_email.getText().toString();
        String mobile = input_mobile.getText().toString();
        String message = input_message.getText().toString();


        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CancelBooking> call = apiService.bigform(
                name,
                email,
                mobile,
                message
        );

        call.enqueue(new retrofit2.Callback<CancelBooking>() {
            @Override
            public void onResponse(Call<CancelBooking>call, retrofit2.Response<CancelBooking> response) {

                try{
                    if (response != null){


                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());


                        if (response.body().getStatus() == 1){

                            /// go to home fragment

                            // Toast.makeText(getActivity(), "Your message sent successfully.", Toast.LENGTH_LONG).show();

                            pDialog.dismiss();

                            dialogButton();

                        }else {

                            pDialog.dismiss();

                            Toast.makeText(getActivity(), "Something error\nTry again", Toast.LENGTH_LONG).show();
                        }



                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<CancelBooking>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());
                pDialog.dismiss();

            }
        });

    }


    private void dialogButton(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your message sent successfully.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Fragment fragment = new BookYourRide();
                        ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.container, fragment);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
