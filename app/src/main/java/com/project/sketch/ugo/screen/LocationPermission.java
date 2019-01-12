package com.project.sketch.ugo.screen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.project.sketch.ugo.R;
import com.project.sketch.ugo.utils.ConnectivityReceiver;

/**
 * Created by developer on 6/3/18.
 */

public class LocationPermission extends AppCompatActivity {


    protected GoogleApiClient mGoogleApiClient;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    boolean buildstatus;

    LocationManager lm;
    boolean isConnected;
    boolean enabledGPS;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_permission);




        Thread_Start();

    }


    public void Thread_Start(){

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                enabledGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isConnected = ConnectivityReceiver.isConnected();


                if (isConnected) {

                    if (enabledGPS){

                        Intent intent = new Intent(LocationPermission.this, Container.class);
                        startActivity(intent);
                        finish();

                    }else {

                        enableLocation();
                    }

                }else {

                    Thread_Start();

                    Toast.makeText(getApplicationContext(), "Please connect to internet", Toast.LENGTH_SHORT).show();
                }

            }
        }, 2000);

    }



    public void enableLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(LocationPermission.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            mGoogleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            mGoogleApiClient.connect();
        }
        buildstatus=false;
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(1 * 1000);
        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:

                        //  Log.i(Constants.TAG, "All location settings are satisfied.");
                        // view first open activity ..........

                        /*if (sharedPref.isRiding()){

                            fragment = new TrackYourRide();
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

                        }else {

                            fragment = new BookYourRide();
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

                        }*/

                        Intent intent = new Intent(LocationPermission.this, Container.class);
                        startActivity(intent);
                        finish();


                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        // Log.i(Constants.TAG, "RESOLUTION_REQUIRED");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(LocationPermission.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {

                            // Ignore the error.
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        //  Log.i(Constants.TAG, "SETTINGS_CHANGE_UNAVAILABLE");

                        break;

                    default:

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        enableLocation();
                        // Log.i(Constants.TAG, "RESULT_OK");

                        break;
                    case Activity.RESULT_CANCELED:

                        // Log.i(Constants.TAG, "RESULT_CANCELED");
                        finish();

                        break;
                }
                break;


        }

        //Log.d(Constants.TAG, "###############3");
    }


}
