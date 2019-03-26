package com.project.sketch.ugo.screen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
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
import com.project.sketch.ugo.fragments.BigCabService;
import com.project.sketch.ugo.fragments.BookYourRide;
import com.project.sketch.ugo.fragments.CabDetails;
import com.project.sketch.ugo.fragments.HelpScreen;
import com.project.sketch.ugo.fragments.HistoryList;
import com.project.sketch.ugo.fragments.TrackYourRide;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel4.CancelBooking;
import com.project.sketch.ugo.httpRequest.apiModel9.OngoingBooking;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.GlobalClass;
import com.project.sketch.ugo.utils.SharedPref;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;

/**
 * Created by Developer on 1/10/18.
 */

public class Container extends AppCompatActivity {


    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private LinearLayout ll_main, ll_header_main;
    TextView tv_username, tv_userphone;
    CircleImageView profile_image;
    RatingBar customer_rating;
   String TAG="container";
    private Fragment fragment = null;

    GlobalClass globalClass;
    SharedPref sharedPref;
    private ProgressDialog pDialog;

    protected GoogleApiClient mGoogleApiClient;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    boolean buildstatus;

    boolean isGpsEnable;

    LocationManager lm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_screen);
        initViews();



    }


    public void initViews(){

        globalClass = (GlobalClass) getApplicationContext();
        sharedPref = new SharedPref(this);

        pDialog  = new ProgressDialog(Container.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Please wait...");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nvView);
        navigationView.setItemIconTintList(null);

        ll_main = (LinearLayout) findViewById(R.id.ll_main);

        // Setup drawer view
        setupDrawerContent(navigationView);

        setupDrawerToggle1();
        //drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);



        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGpsEnable = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);


        /////////////////////

        if (!isGpsEnable){

            enableLocation();

        }

       checkOnGoingBooking();

       /* if (sharedPref.isReachingThePickupLocation()){

            fragment = new TrackYourRide();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

        }else {




        }*/



    }


    @Override
    protected void onResume() {
        setHeaderItem();

        if (sharedPref.isFreeForBooking()){

            fragment = new BookYourRide();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

        }else {

            if (sharedPref.isReachingThePickupLocation()){

                fragment = new TrackYourRide();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

            }

        }


        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void setHeaderItem(){

        /// for header view.........
        View header = navigationView.getHeaderView(0);
        profile_image =  header.findViewById(R.id.profile_image);
        tv_username =  header.findViewById(R.id.tv_username);
        tv_userphone =  header.findViewById(R.id.tv_userphone);
        customer_rating =  header.findViewById(R.id.rating_customer);

        ll_header_main =  header.findViewById(R.id.ll_header_main);
        tv_username.setText(sharedPref.getUserName());
        tv_userphone.setText(sharedPref.getUserPhone());
        Float rate= Float.parseFloat(sharedPref.getCustomer_rating());
        Log.d(TAG, "RAting: "+rate);
        customer_rating.setRating(rate);
        if (!sharedPref.getUserImage().isEmpty()){

            Picasso.with(this).load(sharedPref.getUserImage()).error(R.mipmap.avatar).into( profile_image, new Callback() {
                @Override
                public void onSuccess() {
                    //  Log.d("TAG", "onSuccess");
                }
                @Override
                public void onError() {
                    //  Toast.makeText(mactivity, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });

        }


        ll_header_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_profile = new Intent(Container.this, EditProfile.class);
                startActivity(intent_profile);

            }
        });


    }


    private void setupDrawerToggle1(){

        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                ll_main.setTranslationX(slideOffset * drawerView.getWidth());
                mDrawer.bringChildToFront(drawerView);
                mDrawer.requestLayout();
            }
        };
        mDrawer.setDrawerListener(drawerToggle);


    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private void setupDrawerContent(NavigationView navigationView) {

        setFirstPositionSelected();

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        if (menuItem.isChecked())
                            menuItem.setChecked(false);
                        else
                            menuItem.setChecked(true);

                        refreshNavMenu();
                        setNavMenuItemColor(menuItem);

                        selectDrawerItem(menuItem);

                        return true;
                    }
                });
    }

    private void setFirstPositionSelected(){
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            if (i == 0){
                MenuItem item = navigationView.getMenu().getItem(i);
                SpannableString span = new SpannableString(item.getTitle());
                span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.green)), 0, span.length(), 0);
                item.setTitle(span);
            }else {
                MenuItem item = navigationView.getMenu().getItem(i);
                SpannableString span = new SpannableString(item.getTitle());
                span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, span.length(), 0);
                item.setTitle(span);

            }

        }

    }


    private void refreshNavMenu() {
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            MenuItem item = navigationView.getMenu().getItem(i);
            SpannableString span = new SpannableString(item.getTitle());
            span.setSpan(new ForegroundColorSpan(Color.BLACK), 0, span.length(), 0);
            item.setTitle(span);
        }
    }

    private void setNavMenuItemColor(MenuItem item) {
        SpannableString span = new SpannableString(item.getTitle());
        span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.green)), 0, span.length(), 0);
        item.setTitle(span);
    }


    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked

        fragment = null;

        // Class fragmentClass = null;
        switch(menuItem.getItemId()) {
            case R.id.book_your_ride:

                menuItem.setChecked(true);

                if (globalClass.DRAWER_FRAGMENT_ID != menuItem.getItemId()){
                    globalClass.DRAWER_FRAGMENT_ID = menuItem.getItemId();

                    enableLocation();
                    fragment = new BookYourRide();

                }

               // break;
           /* case R.id.track_your_ride:

                menuItem.setChecked(true);

                if (globalClass.DRAWER_FRAGMENT_ID != menuItem.getItemId()){
                    globalClass.DRAWER_FRAGMENT_ID = menuItem.getItemId();

                    if (sharedPref.isRiding()){

                        fragment = new TrackYourRide();

                    }else {

                        Toast.makeText(getApplicationContext(), "You are not riding now", Toast.LENGTH_LONG).show();

                    }




                }*/

                break;
            case R.id.history:

                menuItem.setChecked(true);

                if (globalClass.DRAWER_FRAGMENT_ID != menuItem.getItemId()){
                    globalClass.DRAWER_FRAGMENT_ID = menuItem.getItemId();

                    fragment = new HistoryList();

                }

                break;


            case R.id.cabDetails:

                menuItem.setChecked(true);

                if (globalClass.DRAWER_FRAGMENT_ID != menuItem.getItemId()){
                    globalClass.DRAWER_FRAGMENT_ID = menuItem.getItemId();

                    fragment = new CabDetails();

                }

                break;


            case R.id.review:

                menuItem.setChecked(true);

                if (globalClass.DRAWER_FRAGMENT_ID != menuItem.getItemId()){
                    globalClass.DRAWER_FRAGMENT_ID = menuItem.getItemId();


                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }


                }
                break;

            case R.id.missedcall:

                menuItem.setChecked(true);

                if (globalClass.DRAWER_FRAGMENT_ID != menuItem.getItemId()){
                    globalClass.DRAWER_FRAGMENT_ID = menuItem.getItemId();

                    try {

                        Intent myIntent = new Intent(Intent.ACTION_CALL);
                        String phNum = "tel:" + sharedPref.getMissedCall_number();
                        myIntent.setData(Uri.parse(phNum));
                        startActivity( myIntent);

                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }


                }
                break;

            /*case R.id.settings:

                menuItem.setChecked(true);

                if (globalClass.DRAWER_FRAGMENT_ID != menuItem.getItemId()){
                    globalClass.DRAWER_FRAGMENT_ID = menuItem.getItemId();





                }
                break;*/

           /* case R.id.big_cab:

                menuItem.setChecked(true);

                if (globalClass.DRAWER_FRAGMENT_ID != menuItem.getItemId()) {
                    globalClass.DRAWER_FRAGMENT_ID = menuItem.getItemId();


                    fragment = new BigCabService();

                }

                break;*/

            case R.id.help:

                menuItem.setChecked(true);

                if (globalClass.DRAWER_FRAGMENT_ID != menuItem.getItemId()){
                    globalClass.DRAWER_FRAGMENT_ID = menuItem.getItemId();

                    fragment = new HelpScreen();

                }

                break;

            default:

        }

        if (fragment != null) {

            try {

            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

            // Highlight the selected item has been done by NavigationView
            // menuItem.setChecked(true);
            // Set action bar title

            setTitle(menuItem.getTitle());

            mDrawer.closeDrawers();

        }else {
            mDrawer.closeDrawers();
        }


    }


    public void enableLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(Container.this)
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

                            if (sharedPref.isRiding()){

                                fragment = new TrackYourRide();
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

                            }else {

                                fragment = new BookYourRide();
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

                            }

                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                       // Log.i(Constants.TAG, "RESOLUTION_REQUIRED");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(Container.this, REQUEST_CHECK_SETTINGS);

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


    ///////////////////////////////////////////////////


    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            globalClass.PICUP_ADDRESS = "";
            globalClass.PICKUP_LATLNG = null;
            globalClass.DROP_ADDRESS = "";
            globalClass.DROP_LATLNG = null;
            globalClass.KEY_of_travel = "";

            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 3000);
    }


    ///////////  Check if On going booking ....

    public void checkOnGoingBooking(){

        pDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<OngoingBooking> call = apiService.checkOngoingBookig(sharedPref.getUserId());

        call.enqueue(new retrofit2.Callback<OngoingBooking>() {
            @Override
            public void onResponse(Call<OngoingBooking>call,
                                   retrofit2.Response<OngoingBooking> response) {

                try {

                    if (response != null){

                        Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                        Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            String booking_status = response.body().getDriver().getBooking_status();

                            globalClass.setDriver(response.body().getDriver());

                            if (booking_status.equals("accepted")){

                                fragment = new BookYourRide();
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();


                            }else if (booking_status.equals("reached")){

                                fragment = new BookYourRide();
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();



                            }else if (booking_status.equals("cancelled")){

                                fragment = new BookYourRide();
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();



                            }else if (booking_status.equals("started")){

                                fragment = new TrackYourRide();
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();


                            }else if (booking_status.equals("end")){


                                fragment = new BookYourRide();
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();


                            }


                        }else if (response.body().getStatus() == 2){

                            fragment = new BookYourRide();
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();


                        }

                        pDialog.dismiss();
                    }

                }catch (Exception e){
                    e.printStackTrace();

                }

            }

            @Override
            public void onFailure(Call<OngoingBooking>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

            }
        });




    }


}
