package com.project.sketch.ugo.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.project.sketch.ugo.R;
import com.project.sketch.ugo.adapter.RecyclerCabAdapter;
import com.project.sketch.ugo.database.DatabaseHelper;
import com.project.sketch.ugo.database.FavDataModel;
import com.project.sketch.ugo.datamodel.CabVariant;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel2.Cab;
import com.project.sketch.ugo.httpRequest.apiModel2.CabList;
import com.project.sketch.ugo.httpRequest.apiModel2.Driver;
import com.project.sketch.ugo.screen.CitytaxiCabBooking;
import com.project.sketch.ugo.screen.LocationSearch;
import com.project.sketch.ugo.screen.OutstationCabBooking;
import com.project.sketch.ugo.screen.RentalCabBooking;
import com.project.sketch.ugo.screen.SpecialFareDialog;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.GPSTracker;
import com.project.sketch.ugo.utils.GlobalClass;
import com.project.sketch.ugo.utils.LocationAddress;
import com.project.sketch.ugo.utils.SharedPref;
import com.rampo.updatechecker.UpdateChecker;
import com.rampo.updatechecker.notice.Notice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;

import static com.project.sketch.ugo.utils.Constants.TAG;

/**
 * Created by Developer on 1/10/18.
 */

public class BookYourRide extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnMapLoadedCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        RecyclerCabAdapter.ViewClickListener{


    RelativeLayout rl_loader;
    Thread splashTread;

    RelativeLayout rl_pickup,rl_drop,rl_ride_later,rl_ride_now;
    TextView tv_pickup_address,tv_drop_address;
    ImageView image_mylocation, image_cross , image_pin, image_fav1, image_fav2, image_search1, image_search2;
    RelativeLayout ll_bottom;
    LayoutInflater inflater;

    RecyclerView recyclerview_cab;
    RecyclerCabAdapter recyclerCabAdapter;
    RadioGroup radioGroup;
    RadioButton radio_city, radio_outstation, radio_rental, radio_special_fare;
    RelativeLayout rl_2_button, rl_1_button, rl_radio, rl_continue;
    String image;

    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;

    private GoogleMap map = null;
    GPSTracker gpsTracker;
    GlobalClass globalClass;
    SharedPref sharedPref;

    SupportMapFragment mapFrag;
    LocationManager lm;
    Geocoder geoCoder;

    String st_picup_address = "";
    LatLng mPosition;

    boolean is_camera_move = false;
    boolean isSelectPickup, isSelectDrop;
    boolean isPickupFill, isDropFill;

    Animation show_animation, hide_animation, zoom_in, zoom_out;
    Animation slideUpAnimation, slideDownAnimation, slide_lefttoright, slide_righttoleft;
    Animation rotate_view;

    private static final int REQUEST_CODE_SEARCH_LOCATION = 2200;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    float zoom = 16;
    String type, title;
    int selected_cab_position = 0;
    String booking_type;
    int root_distance = 0;
    boolean firstTimeApiCall = true;

    private List<Cab> cab_Details_City_Taxi;
    private List<Cab> cab_Details_OutStation;
    private List<Cab> cab_Details_Rental;

    SpecialFareDialog specialFareDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_bookyourride, container, false);
     //
        InitViews(rootView);

        UpdateChecker checker = new UpdateChecker(getActivity());
        checker.setNotice(Notice.NOTIFICATION);
        checker.start();

        return rootView;
    }

    public void InitViews(View view){

        gpsTracker = new GPSTracker(getActivity());
        globalClass = (GlobalClass)getActivity().getApplicationContext();
        sharedPref = new SharedPref(getActivity());

       /* if (globalClass.getDriver().getBooking_status().equals("accepted")){

            Intent intent = new Intent(getActivity(), CitytaxiCabBooking.class);
            intent.putExtra("key", "ongoing");
            startActivity(intent);

        }else {*/

            globalClass.PICUP_ADDRESS = "";
            globalClass.DROP_ADDRESS = "";
            globalClass.KEY_of_travel = Constants.key_pickup;
            sharedPref.setFreeForBooking(false);

            mapFrag = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);


            //Initializing google api client
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                }
            }
            else {

            }

            geoCoder = new Geocoder(getActivity(), Locale.getDefault());
            lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0.1f , this);

            //((AppCompatActivity)getActivity()).getSupportActionBar().hide();


            tv_pickup_address = view.findViewById(R.id.tv_pickup_address);
            tv_pickup_address.setText("Getting Address...\n");
            tv_drop_address = view.findViewById(R.id.tv_drop_address);

            tv_drop_address.setText("Select Drop Address\n");

            ll_bottom = view.findViewById(R.id.ll_bottom);
            ll_bottom.setVisibility(View.GONE);
            rl_loader = view.findViewById(R.id.rl_loader);
            rl_loader.setVisibility(View.VISIBLE);

            rl_ride_later = view.findViewById(R.id.rl_ride_later);
            rl_ride_now = view.findViewById(R.id.rl_ride_now);
            rl_continue = view.findViewById(R.id.rl_continue);


            rl_pickup = (RelativeLayout)view.findViewById(R.id.rl_pickup);
            rl_drop = (RelativeLayout)view.findViewById(R.id.rl_drop);
            rl_radio = (RelativeLayout)view.findViewById(R.id.rl_radio);
            rl_radio.setVisibility(View.GONE);
            //rl_radio.getBackground().setAlpha(160);

            rl_2_button = (RelativeLayout) view.findViewById(R.id.rl_2_button);
            rl_2_button.setVisibility(View.VISIBLE);
            rl_1_button = (RelativeLayout) view.findViewById(R.id.rl_1_button);
            rl_1_button.setVisibility(View.GONE);

            //rl_pickup.getBackground().setAlpha(150);
            //rl_drop.getBackground().setAlpha(150);

            //rl_drop.setVisibility(View.GONE);

            image_mylocation = (ImageView)view.findViewById(R.id.image_mylocation);
            image_mylocation.setVisibility(View.GONE);

            image_cross = (ImageView)view.findViewById(R.id.image_cross);
            image_pin = (ImageView)view.findViewById(R.id.image_pin);
            image_fav1 = (ImageView)view.findViewById(R.id.image_fav1);
            image_fav2 = (ImageView)view.findViewById(R.id.image_fav2);

            image_search1 = (ImageView)view.findViewById(R.id.image_search1);
            image_search2 = (ImageView)view.findViewById(R.id.image_search2);

            image_cross.setVisibility(View.GONE);

            inflater = getActivity().getLayoutInflater();
            //innerlayout = (LinearLayout)view.findViewById(R.id.innerLay);

            recyclerview_cab = (RecyclerView) view.findViewById(R.id.recyclerview_cab);

            radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
            radio_city = (RadioButton) view.findViewById(R.id.radio_city);
            radio_outstation = (RadioButton) view.findViewById(R.id.radio_outstation);
            radio_rental = (RadioButton) view.findViewById(R.id.radio_rental);
            radio_special_fare = (RadioButton) view.findViewById(R.id.radio_special_fare);



            isSelectPickup = true;
            isSelectDrop = false;
            isPickupFill = true;
            isDropFill = false;

            booking_type = getActivity().getResources().getString(R.string.CityTaxi);



            show_animation = AnimationUtils.loadAnimation(getActivity(), R.anim.in_animation);
            hide_animation = AnimationUtils.loadAnimation(getActivity(), R.anim.out_animation);

            slideUpAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_animation);
            slideDownAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down_animation);

            slide_righttoleft = AnimationUtils.loadAnimation(getActivity(), R.anim.right_to_left_anim);
            slide_lefttoright = AnimationUtils.loadAnimation(getActivity(), R.anim.left_to_right_anim);

            zoom_in = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in);
            zoom_out = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_out);

            specialFareDialog = new SpecialFareDialog(getActivity());


            createLocationRequest();



            buttonOnClick();

      //  }


    }


    private void buttonOnClick(){

        image_search1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                globalClass.KEY_of_travel = Constants.key_pickup;
                Intent intent = new Intent(getActivity(), LocationSearch.class);
                intent.putExtra("key", "pickup");
                startActivityForResult(intent, REQUEST_CODE_SEARCH_LOCATION);
                (getActivity()).overridePendingTransition(R.anim.slide_up, R.anim.stay);


            }
        });

        image_search2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                globalClass.KEY_of_travel = Constants.key_drop;
                Intent intent = new Intent(getActivity(), LocationSearch.class);
                intent.putExtra("key", "drop");
                startActivityForResult(intent, REQUEST_CODE_SEARCH_LOCATION);
                (getActivity()).overridePendingTransition(R.anim.slide_up, R.anim.stay);

            }
        });


        rl_pickup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                image_mylocation.setVisibility(View.VISIBLE);

                if (isPickupFill){

                   // isPickupFill = false;
                    isSelectPickup = true;
                    isSelectDrop = false;

                    setPickLocationMarker(map);

                }

            }
        });


        rl_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                image_mylocation.setVisibility(View.GONE);

                if (isDropFill){

                   // isDropFill = false;

                    isSelectPickup = false;
                    isSelectDrop = true;


                    setDropLocationMarker(map);

                }else {

                    globalClass.KEY_of_travel = Constants.key_drop;
                    Intent intent = new Intent(getActivity(), LocationSearch.class);
                    intent.putExtra("key", "drop");
                    startActivityForResult(intent, REQUEST_CODE_SEARCH_LOCATION);
                    ((Activity)getActivity()).overridePendingTransition(R.anim.slide_up, R.anim.stay);

                }
            }
        });


        image_mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                is_camera_move = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        map.setMyLocationEnabled(true);
                    }
                }
                else {

                    map.setMyLocationEnabled(true);
                }


                Location location = map.getMyLocation();

                if (location != null) {

                    LatLng target = new LatLng(location.getLatitude(), location.getLongitude());

                    CameraPosition position = new CameraPosition.Builder().target(target).zoom(16).build();
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(position));

                }

            }
        });


        rl_ride_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String rental = getActivity().getResources().getString(R.string.Rental);

                if (booking_type.matches(rental)){

                    Intent intent_rental = new Intent(getActivity(), RentalCabBooking.class);
                    startActivity(intent_rental);

                }else {

                    if (globalClass.DROP_ADDRESS.equals("") ){

                        Toast.makeText(getActivity(), "Select Drop address", Toast.LENGTH_LONG).show();
                    }else {

                        if (globalClass.DROP_LATLNG != null){
                            globalClass.Later_Ride = "N";
                            Intent intent = new Intent(getActivity(), CitytaxiCabBooking.class);
                            startActivity(intent);

                        }else {

                            tv_drop_address.setText("Select Drop Address\n");
                            globalClass.DROP_ADDRESS = "";

                        }
                    }
                }

            }
        });


        rl_ride_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String rental = getActivity().getResources().getString(R.string.Rental);

                if (rental.matches(booking_type)){

                    showDateTimePicker();

                }else {

                    if (globalClass.DROP_ADDRESS.equals("")){

                        Toast.makeText(getActivity(), "Select Drop address", Toast.LENGTH_LONG).show();

                    }else {

                        showDateTimePicker();

                    }
                }


            }
        });


        image_fav1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFavAddressDialog(globalClass.PICUP_ADDRESS,
                        String.valueOf(globalClass.PICKUP_LATLNG.latitude),
                        String.valueOf(globalClass.PICKUP_LATLNG.longitude));
            }
        });


        image_fav2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!globalClass.DROP_ADDRESS.isEmpty()){

                    showFavAddressDialog(globalClass.DROP_ADDRESS,
                            String.valueOf(globalClass.DROP_LATLNG.latitude),
                            String.valueOf(globalClass.DROP_LATLNG.longitude));
                }

            }
        });


        rl_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!globalClass.DROP_ADDRESS.isEmpty()){

                    Intent intent = new Intent(getActivity(), OutstationCabBooking.class);
                    startActivity(intent);
                }

            }
        });



        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId){

                    case R.id.radio_city:

                        globalClass.isSpecialFareSelect = false;

                        rl_2_button.setVisibility(View.VISIBLE);
                        rl_1_button.setVisibility(View.GONE);
                        rl_drop.setVisibility(View.VISIBLE);
                        rl_continue.setVisibility(View.GONE);

                        booking_type = getActivity().getResources().getString(R.string.CityTaxi);

                        selected_cab_position = 0;
                        processingDriver(cab_Details_City_Taxi);


                        if ((root_distance/1000) > max_citytaxi_distance){
                            globalClass.DROP_ADDRESS = "";
                            globalClass.DROP_LATLNG = null;
                            tv_drop_address.setText("Select Drop Address\n");
                            isDropFill = false;

                            Log.d(TAG, "globalClass.DROP_ADDRESS in 1");
                        }

                        break;
                    case R.id.radio_outstation:

                        globalClass.isSpecialFareSelect = false;

                        rl_1_button.setVisibility(View.VISIBLE);
                        rl_2_button.setVisibility(View.GONE);
                        rl_drop.setVisibility(View.VISIBLE);
                        rl_continue.setVisibility(View.VISIBLE);

                        booking_type = getActivity().getResources().getString(R.string.Outstation);

                        selected_cab_position = 0;
                        processingDriver(cab_Details_OutStation);

                        break;
                    case R.id.radio_rental:

                        globalClass.isSpecialFareSelect = false;

                        rl_2_button.setVisibility(View.VISIBLE);
                        rl_1_button.setVisibility(View.GONE);
                        rl_drop.setVisibility(View.GONE);
                        rl_continue.setVisibility(View.GONE);

                        booking_type = getActivity().getResources().getString(R.string.Rental);

                        selected_cab_position = 0;
                        processingDriver(cab_Details_Rental);


                        break;

                    case R.id.radio_special_fare:

                        booking_type = getActivity().getResources().getString(R.string.SpecialFare);

                        globalClass.isSpecialFareSelect = true;

                        specialFareDialog.show();

                        break;
                }

            }
        });




    }


    @Override
    public void onResume(){

        is_camera_move = false;

        if (globalClass.is_Cancel){
            globalClass.is_Cancel = false;

            isSelectDrop = false;
            isDropFill = false;

            isSelectPickup = true;


            tv_drop_address.setText("Select Drop Address\n");
            setPickLocationMarker(map);

        }

        if (timer != null){
            callTread();
        }

        super.onResume();
    }

    @Override
    public void onPause(){


        is_camera_move = false;

        if (globalClass.KEY_of_travel.equals(Constants.key_drop)){

         //   image_pin.setImageResource(R.mipmap.pin_center_red);
        }else
        if (globalClass.KEY_of_travel.equals(Constants.key_pickup)){

         //   image_pin.setImageResource(R.mipmap.pin_centre_green);
        }


        if (timer != null){
            timer.cancel();
            timer = null;
        }

        super.onPause();
    }

    @Override
    public void onStart() {
        googleApiClient.connect();

        super.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onMapReady(GoogleMap mapp) {

        map = mapp;

        map.setPadding(0, ll_bottom.getHeight(), 0, ll_bottom.getHeight());

        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setRotateGesturesEnabled(true);
        map.getUiSettings().setTiltGesturesEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setScrollGesturesEnabled(true);


        map.setTrafficEnabled(false);

        // map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                map.setMyLocationEnabled(true);
            }
        }
        else {

            map.setMyLocationEnabled(true);
        }



        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
        map.setOnMapLoadedCallback(this);

        mapMovement(map);


    }

    @Override
    public void onCameraMoveStarted(int i) {

        Log.d(TAG, "setOnCameraMoveStartedListener");

        if (is_camera_move){

            if (isSelectPickup){

               // rl_drop.startAnimation(slide_righttoleft);
                rl_drop.setVisibility(View.GONE);

            }else if (isSelectDrop){

               // rl_pickup.startAnimation(slide_righttoleft);
                rl_pickup.setVisibility(View.GONE);

            }


            ll_bottom.startAnimation(hide_animation);
            ll_bottom.setVisibility(View.GONE);

            image_mylocation.startAnimation(slide_righttoleft);
            image_mylocation.setVisibility(View.GONE);
            //image_mylocation.startAnimation(slide_righttoleft);

            image_pin.setVisibility(View.GONE);
            image_cross.setVisibility(View.VISIBLE);

            map.setPadding(0, ll_bottom.getHeight(), 0, ll_bottom.getHeight());

        }


    }

    @Override
    public void onCameraIdle() {
        Log.d(TAG, "setOnCameraIdleListener");
        // Cleaning all the markers.
        if (map != null) {
          //  map.clear();
        }

        mPosition = map.getCameraPosition().target;

        if (is_camera_move){

            if (isSelectPickup){

               // rl_drop.startAnimation(slide_lefttoright);
                rl_drop.setVisibility(View.VISIBLE);

            }else if (isSelectDrop){

               /// rl_pickup.startAnimation(slide_lefttoright);
                rl_pickup.setVisibility(View.VISIBLE);

            }



            if (!firstTimeApiCall){
                ll_bottom.startAnimation(show_animation);
                ll_bottom.setVisibility(View.VISIBLE);

                image_mylocation.startAnimation(slide_lefttoright);
                image_mylocation.setVisibility(View.VISIBLE);
            }


            image_cross.setVisibility(View.GONE);

            //  image_pin.clearAnimation();
            image_pin.setVisibility(View.VISIBLE);


            map.setPadding(0, ll_bottom.getHeight(), 0, ll_bottom.getHeight());

           // zoom = map.getCameraPosition().zoom;

        }

        //zoom = map.getCameraPosition().zoom;

        is_camera_move = true;

        if (isSelectPickup){

            globalClass.PICKUP_LATLNG = mPosition;

          //  getNearestDriverUsingLatLng();

            callTread();

        }else if (isSelectDrop){

            globalClass.DROP_LATLNG = mPosition;

        }


        fetchDistance(globalClass.KEY_of_travel);

        //zoom = map.getCameraPosition().zoom;
    }

    @Override
    public void onMapLoaded() {
        Log.d(TAG, "onMapLoaded");

        getCurrentLocation();
        Location location = map.getMyLocation();

        Log.d(TAG, "location = "+location);

        if (location != null) {
            LatLng target = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition position = new CameraPosition.Builder().target(target).zoom(zoom).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(position));


            ((AppCompatActivity) getActivity()).getSupportActionBar().show();


           // rl_drop.startAnimation(slide_lefttoright);
            rl_drop.setVisibility(View.VISIBLE);


            // zoom = map.getCameraPosition().zoom;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, ">>>>>>>>>>>>>>>>>>");
        Log.d(TAG, ">>>>>>>>>>>>>>>>>>");
        getActivity();
        if(requestCode == REQUEST_CODE_SEARCH_LOCATION && resultCode == Activity.RESULT_OK) {
            //some code
            String key = data.getStringExtra("key");
            globalClass.KEY_of_travel = key;

            Log.d(TAG, "Return - "+key);
            Log.d(TAG, "Return - "+globalClass.PICUP_ADDRESS);
            Log.d(TAG, "Return - "+globalClass.DROP_ADDRESS);

            map.setOnCameraMoveStartedListener(null);
            map.setOnCameraIdleListener(null);

            if (key.equals("pickup")){

                tv_pickup_address.setText("Pick Address:\n"+globalClass.PICUP_ADDRESS);

                setPickLocationMarker(map);

                isPickupFill = true;

                isSelectDrop = false;
                isSelectPickup = true;

            }else if (key.equals("drop")){

                tv_drop_address.setText("Drop Address:\n"+globalClass.DROP_ADDRESS);

                setDropLocationMarker(map);

                isDropFill = true;

                isSelectPickup = false;
                isSelectDrop = true;

            }



        }
    }


    private void mapMovement(final GoogleMap mapp){

        mapp.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                Log.d(TAG, "setOnMapLongClickListener");

            }
        });

    }


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");

                    break;
                default:
                    locationAddress = null;
            }

            if (locationAddress != null){

                st_picup_address = locationAddress;


                if (isSelectPickup){

                    tv_pickup_address.setText("Pickup Address: \n" + locationAddress);

                    globalClass.PICUP_ADDRESS = st_picup_address;

                }else if (isSelectDrop){

                    rl_drop.setVisibility(View.VISIBLE);

                    globalClass.DROP_ADDRESS = st_picup_address;

                    tv_drop_address.setText("Drop Address: \n"+globalClass.DROP_ADDRESS);

                }

            }

        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.d(TAG, strReturnedAddress.toString());
            } else {
                Log.w(TAG, "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "Cannot get Address!");
        }
        return strAdd;
    }


    @Override
    public void onLocationChanged(Location location) {


        // map.clear();
        //  MarkerOptions marker = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()));
        //  marker.title("Current location");
        // marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));



        // Log.d("RYD", "onLocationChanged  lat >> "+location.getLatitude());
        //  Log.d("RYD", "onLocationChanged  lon >> "+location.getLongitude());


        //  CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        //  CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        //  map.moveCamera(center);
        // map.animateCamera(zoom);


        /*String filterAddress = "";


        try {
            List<Address> addresses =
                    geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses.size() > 0) {
                for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++)
                    filterAddress += addresses.get(0).getAddressLine(i) + " ";


                Log.d("TAG", "filterAddress >> "+filterAddress);


            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e2) {
            // TODO: handle exception
            e2.printStackTrace();
        }*/



    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub
    }


    public void showFavAddressDialog(final String fav_address, final String lat, final String lang){

        final DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        final FavDataModel favDataModel = new FavDataModel();


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fav_address_popup, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.radioGroup);
        final EditText et_title = (EditText) dialogView.findViewById(R.id.et_title);
        final ImageView iv_icons = (ImageView) dialogView.findViewById(R.id.iv_icons);
        TextView tv_address = (TextView) dialogView.findViewById(R.id.tv_address);
        TextView tv_add = (TextView) dialogView.findViewById(R.id.tv_add);
        TextView tv_cancel = (TextView) dialogView.findViewById(R.id.tv_cancel);

        et_title.setVisibility(View.GONE);
        type = "home";
        title = "Home";
        tv_address.setText(fav_address);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_home:
                        et_title.setVisibility(View.GONE);
                        iv_icons.setImageResource(R.mipmap.icon_home);
                        type = "home";
                        title = "Home";


                        break;

                    case R.id.radio_work:
                        et_title.setVisibility(View.GONE);
                        iv_icons.setImageResource(R.mipmap.icon_office);
                        type = "work";
                        title = "Work";

                        break;

                    case R.id.radio_others:
                        et_title.setVisibility(View.VISIBLE);
                        iv_icons.setImageResource(R.mipmap.green_marker);
                        type = "others";
                        title = et_title.getText().toString();

                        break;
                    default:
                        break;

                }

            }
        });


        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                favDataModel.setTYPE(type);
                if (type.matches("others")){
                    favDataModel.setTITLE(et_title.getText().toString());
                }else {
                    favDataModel.setTITLE(title);
                }

                favDataModel.setADDRESS(fav_address);
                favDataModel.setLATITUDE(lat);
                favDataModel.setLONGITUDE(lang);

                databaseHelper.insertContactData(favDataModel);

                alertDialog.dismiss();
            }
        });

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });




    }


    @Override
    public void onConnected(Bundle bundle) {
       // getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void setDropLocationMarker(GoogleMap googleMap){

        image_pin.setImageResource(R.mipmap.pin_red_centre);

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target(globalClass.DROP_LATLNG)
                .zoom(zoom)
                .build()));

        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);

    }

    private void setPickLocationMarker(GoogleMap googleMap){

        image_pin.setImageResource(R.mipmap.pin_green_centre);

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .target((globalClass.PICKUP_LATLNG))
                .zoom(zoom)
                .build()));

        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);

    }


    //////////////////////////////////

    private SimpleDateFormat mFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {

            Log.d(TAG, "Time >> "+mFormatter.format(date));
            globalClass.Booking_later_time = mFormatter.format(date);
            globalClass.Later_Ride = "Y";

            String rental = getActivity().getResources().getString(R.string.Rental);

            if (rental.matches(booking_type)){

                Intent intent_rental = new Intent(getActivity(), RentalCabBooking.class);
                startActivity(intent_rental);

            }else {

                Intent intent = new Intent(getActivity(), CitytaxiCabBooking.class);
                startActivity(intent);
            }

        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {
          //  Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
        }
    };

    private void showDateTimePicker(){

        new SlideDateTimePicker.Builder(getChildFragmentManager())
                .setListener(listener)
                .setInitialDate(new Date())
                .setMinDate(new Date())
                //.setMaxDate(maxDate)
                //.setIs24HourTime(true)
                //.setTheme(SlideDateTimePicker.HOLO_DARK)
                //.setIndicatorColor(Color.parseColor("#990000"))
                .build()
                .show();

    }

    //////////////////////////////////

    //Getting current location
    private void getCurrentLocation() {
        map.clear();
        //Creating a location object

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

            }
        }
        else {

        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            //moving the map to location

            Log.d(TAG, "latitude > "+latitude);
            Log.d(TAG, "longitude > "+longitude);

            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
            CameraUpdate zoom1 = CameraUpdateFactory.zoomTo(zoom);

            map.moveCamera(center);
            map.animateCamera(zoom1);

        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////

    private Timer timer;
    private void callTread(){

        int delay = 1000; // delay for 1 sec.
        int period = 10 * 1000; // repeat every 10 sec.

        if (timer == null){

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask()
            {
                public void run()
                {

                    getNearestDriverUsingLatLng();

                }
            }, delay, period);
        }

    }


    private void getNearestDriverUsingLatLng(){

        Log.d(TAG, "getNearestDriverUsingLatLng");

        cab_Details_City_Taxi = new ArrayList<>();
        cab_Details_OutStation = new ArrayList<>();
        cab_Details_Rental = new ArrayList<>();



        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CabList> call = apiService.getNearestDriver(
                String.valueOf(globalClass.PICKUP_LATLNG.latitude),
                String.valueOf(globalClass.PICKUP_LATLNG.longitude)
        );

        call.enqueue(new Callback<CabList>() {
            @Override
            public void onResponse(Call<CabList>call, retrofit2.Response<CabList> response) {


                try {
                    if (response != null){

                        Log.d(TAG, "onResponse: " + response.body().getMessage());
                        Log.d(TAG, "onResponse: " + response.body().getStatus());

                        if (response.body().getStatus() == 1){

                            sharedPref.saveMissedCallNumber(response.body().getMissed_call_number());

                            rl_radio.setVisibility(View.VISIBLE);

                            if (firstTimeApiCall){

                                rl_loader.startAnimation(hide_animation);
                                rl_loader.setVisibility(View.GONE);

                                ll_bottom.startAnimation(show_animation);
                                ll_bottom.setVisibility(View.VISIBLE);
                                image_mylocation.setVisibility(View.VISIBLE);

                                firstTimeApiCall = false;
                            }

                            processingCabData(response.body());

                        }



                    }



                } catch (Exception e){
                    e.printStackTrace();

                }


            }

            @Override
            public void onFailure(Call<CabList>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());

            }
        });

    }


    private void processingCabData(CabList cabData){

        String cityTaxi = getActivity().getResources().getString(R.string.CityTaxi);
        String outstation = getActivity().getResources().getString(R.string.Outstation);
        String rental = getActivity().getResources().getString(R.string.Rental);

        cab_Details_City_Taxi = cabData.getCab_Details_City_Taxi();
        cab_Details_OutStation = cabData.getCab_Details_OutStation();
        cab_Details_Rental = cabData.getCab_Details_Rental();


        if (booking_type.matches(cityTaxi)){

            processingDriver(cab_Details_City_Taxi);

            map.setPadding(0, ll_bottom.getHeight(), 0, ll_bottom.getHeight());

        }else if (booking_type.matches(outstation)){

            processingDriver(cab_Details_OutStation);

            map.setPadding(0, ll_bottom.getHeight(), 0, ll_bottom.getHeight());

        }else if (booking_type.matches(rental)){

            processingDriver(cab_Details_Rental);

            map.setPadding(0, ll_bottom.getHeight(), 0, ll_bottom.getHeight());

        }

    }


    private void processingDriver(List<Cab> cabList){

        List<Driver> driverList = new ArrayList<Driver>();
        String cab_marker = "";

        try {

            if (cabList.size() > 0){

                for (int j = 0; j < cabList.get(selected_cab_position)
                        .getDriver_Array().size(); j++){

                    driverList.add(cabList.get(selected_cab_position)
                            .getDriver_Array().get(j));

                    cab_marker = cabList.get(selected_cab_position).getSelect_image();

                }

                setDataOfCabs(cabList);

                setDriverInMap(driverList, cab_marker);

            }

        }catch (Exception e){
            e.printStackTrace();

        }




    }


    private void setDriverInMap(List<Driver> driverList, String cab_marker){

        map.clear();

        for (int i = 0; i < driverList.size(); i++){

            double lati = Double.parseDouble(driverList.get(i).getLat());
            double longi = Double.parseDouble(driverList.get(i).getLng());

           // createMarker(lati, longi);
          //  Log.d(TAG, "setDriverInMap: "+image);
            LatLng point = new LatLng(lati, longi);
          //  drawMarker(point, cab_marker);

            JSONObject object = new JSONObject();

            try {

                object.put("url", cab_marker);
                object.put("lat", driverList.get(i).getLat());
                object.put("lng", driverList.get(i).getLng());

            }catch (Exception e){
                e.printStackTrace();
            }

            new DrawMarkerAsync().execute(object.toString());

        }

    }


    private class DrawMarkerAsync extends AsyncTask<String, Void, String> {
        BitmapDescriptor icon;
        String url_image;
        double lati;
        double longi;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JSONObject object = new JSONObject(params[0]);

                url_image = object.getString("url");
                lati = object.getDouble("lat");
                longi = object.getDouble("lng");

            }catch (Exception e){
                e.printStackTrace();
            }

            URL url = null;
            try {
                url = new URL(url_image);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                if (image.equals("")) {
                    Toast.makeText(getActivity(), "no cab found", Toast.LENGTH_SHORT).show();
                } else {
                    Bitmap resized = Bitmap.createScaledBitmap(image, (int) (image.getWidth() * 0.8), (int) (image.getHeight() * 0.8), true);
                    globalClass.cab_image = resized;
                    //  Bitmap dest = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
                    icon = BitmapDescriptorFactory.fromBitmap(resized);
                    //  Bitmap bm = BitmapFactory.decodeFile(cab_marker);

                }
            } catch(IOException e){
                e.printStackTrace();
                Toast.makeText(getActivity(), "Image not found", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                e.printStackTrace();
            }

            // Bitmap bmp = BitmapFactory.decodeStream(cab_marker.openConnection().getInputStream());

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            LatLng point = new LatLng(lati, longi);
            Log.e(TAG, "drawMarker: " + icon);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(point);
            markerOptions.anchor(0.5f, 0.5f);
            markerOptions.icon(icon);

            map.addMarker(markerOptions);
        }



    }

    private void drawMarker(LatLng point, String cab_marker) {


        }


    private void setDataOfCabs(final List<Cab> cabList){

        recyclerCabAdapter = null;
        ArrayList<CabVariant> horizontalList = new ArrayList<>();

        CabVariant cabVariant;

        for (int i = 0; i < cabList.size(); i++){
            cabVariant = new CabVariant();
            cabVariant.setCab_name(cabList.get(i).getCab_name());
            cabVariant.setCab_id(cabList.get(i).getCab_id());
                Log.d(TAG, "cab id"+cabList.get(i).getCab_id());
            cabVariant.setSelectImage(cabList.get(i).getSelect_image());
            cabVariant.setUnselectImage(cabList.get(i).getUnselect_image());
            cabVariant.setNoOfcab(cabList.get(i).getDriver_Array().size());


            List< Double> doubleList = new ArrayList<Double>();

            for (int j = 0; j < cabList.get(i).getDriver_Array().size(); j++){

                Driver driver = cabList.get(i).getDriver_Array().get(j);

                doubleList.add(Double.parseDouble(driver.getDistance()));

            }

            if (doubleList.size() > 0){

                double min_dis = Collections.min(doubleList);
                cabVariant.setCab_reaching_time(String.valueOf(getReachedTime(min_dis)));

              //  Log.d(Constants.TAG, "min_dis = "+min_dis);
              //  Log.d(Constants.TAG, "reaching_time = "+cabVariant.getCab_reaching_time());

            }else {

                cabVariant.setCab_reaching_time("0");
            }

            horizontalList.add(cabVariant);


        }

       // Log.d(Constants.TAG, "horizontalList = "+horizontalList.size());

        if (recyclerCabAdapter != null){

            recyclerCabAdapter.notifyData();

        }else {

            recyclerCabAdapter = new RecyclerCabAdapter(getActivity(), horizontalList);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            recyclerview_cab.setLayoutManager(horizontalLayoutManager);
            recyclerview_cab.setItemAnimator(new DefaultItemAnimator());
            recyclerview_cab.setAdapter(recyclerCabAdapter);

            recyclerCabAdapter.setViewClickListener(this);

        }

    }


    @Override
    public void onImageClicked(int position) {

        selected_cab_position = position;

        String cityTaxi = getActivity().getResources().getString(R.string.CityTaxi);
        String outstation = getActivity().getResources().getString(R.string.Outstation);
        String rental = getActivity().getResources().getString(R.string.Rental);

        if (booking_type.matches(cityTaxi)){

            if (cab_Details_City_Taxi.size() != 0){

                List<Driver> driverList = cab_Details_City_Taxi.get(position).getDriver_Array();
                String cab_marker= cab_Details_City_Taxi.get(position).getSelect_image();

                setDriverInMap(driverList,cab_marker);
            }
        }else if (booking_type.matches(outstation)){

            if (cab_Details_OutStation.size() != 0){

                List<Driver> driverList = cab_Details_OutStation.get(position).getDriver_Array();
                String cab_marker= cab_Details_OutStation.get(position).getSelect_image();
                setDriverInMap(driverList,cab_marker);
            }

        }else if (booking_type.matches(rental)){

            if (cab_Details_Rental.size() != 0){

                List<Driver> driverList = cab_Details_Rental.get(position).getDriver_Array();
                String cab_marker= cab_Details_Rental.get(position).getSelect_image();
                setDriverInMap(driverList,cab_marker);
            }
        }

    }


    private int getReachedTime(double miles_dis){
        // 10 km/hour
        int average_speed = 10;
        int minute = 60;
        double km = milesTokm(miles_dis);
        //int time = (int)((minute * km) / average_speed);
        int time = (int) Math.round((minute * km) / average_speed);

        return time;
    }

    private static double milesTokm(double distanceInMiles) {
        return distanceInMiles * 1.60934;
    }


    /////////////////////////////////////

    //// check distance...

    int max_citytaxi_distance = 40;
    private static final String API_KEY = "AIzaSyDIKxEj9TzalzfhG7r5tA83EbmZCWvXeow";
    public void fetchDistance(final String key){

        final String rental = getActivity().getResources().getString(R.string.Rental);
        final String city_taxi = getActivity().getResources().getString(R.string.CityTaxi);
        final String special_fare = getActivity().getResources().getString(R.string.SpecialFare);

        if (booking_type.matches(rental)){
            radio_rental.setChecked(true);
        }else if (booking_type.matches(special_fare)){
            radio_special_fare.setChecked(true);
        }else if (booking_type.matches(city_taxi)){
            radio_city.setChecked(true);
        }

        Log.d(TAG, "booking_type = "+booking_type);

        if (globalClass.DROP_LATLNG == null){

            LocationAddress locationAddress = new LocationAddress();
            locationAddress.getAddressFromLocation(mPosition.latitude, mPosition.longitude,
                    getActivity(), new GeocoderHandler());

        }else {

            String URL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="
                    + globalClass.PICKUP_LATLNG.latitude +","
                    + globalClass.PICKUP_LATLNG.longitude
                    + "&destinations="
                    + globalClass.DROP_LATLNG.latitude + ","
                    + globalClass.DROP_LATLNG.longitude
                    + "&mode=driving&language=EN"
                    + "&key=" + API_KEY;

            Log.d(TAG, "URL - "+URL);

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(URL, new JsonHttpResponseHandler(){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    //Here response will be received in form of JSONObject

                     Log.i(TAG,"Server response - "+response );

                    try {
                        JSONArray rows = response.getJSONArray("rows");
                        JSONObject object = rows.getJSONObject(0);
                        JSONArray elements = object.getJSONArray("elements");
                        JSONObject object1 = elements.getJSONObject(0);
                        JSONObject distance = object1.getJSONObject("distance");
                        String status = object1.getString("status");

                        int value = distance.optInt("value");
                        root_distance = value;

                        if (status.equalsIgnoreCase("ZERO_RESULTS")){

                            LocationAddress locationAddress = new LocationAddress();
                            locationAddress.getAddressFromLocation(mPosition.latitude, mPosition.longitude,
                                    getActivity(), new GeocoderHandler());


                        }else if (status.equalsIgnoreCase("OK")){

                            /*LocationAddress locationAddress = new LocationAddress();
                            locationAddress.getAddressFromLocation(mPosition.latitude, mPosition.longitude,
                                    getActivity(), new GeocoderHandler());*/


                            JSONArray origin_addresses =
                                    response.getJSONArray("origin_addresses");
                            String string_pick = origin_addresses.getString(0);


                            JSONArray destination_addresses =
                                    response.getJSONArray("destination_addresses");
                            String string_drop = destination_addresses.getString(0);


                            if (isSelectPickup){
                                globalClass.PICUP_ADDRESS = string_pick;
                                tv_pickup_address.setText("Pick Address:\n"+string_pick);
                            }else if (isSelectDrop){
                                globalClass.DROP_ADDRESS = string_drop;
                                tv_drop_address.setText("Drop Address:\n"+string_drop);
                            }


                            if ((value/1000) > max_citytaxi_distance){

                                if ((string_pick.contains("Bhubaneshwar")
                                        || string_pick.contains("Cuttack"))
                                        && (string_drop.contains("Bhubaneshwar")
                                        || string_drop.contains("Cuttack"))){

                                    if (booking_type.matches(rental)){
                                        radio_rental.setChecked(true);
                                    }else if (booking_type.matches(special_fare)){
                                        radio_special_fare.setChecked(true);
                                    }else if (booking_type.matches(city_taxi)){
                                        radio_city.setChecked(true);
                                    }

                                    Log.d(TAG, "Y = Bhubaneshwar/Cuttack");

                                }else {

                                    radio_outstation.setChecked(true);

                                    Log.d(TAG, "N = Bhubaneshwar/Cuttack");
                                }


                            }else if ((value/1000) <= max_citytaxi_distance){

                                Log.d(TAG, "Distance below 40 km");

                            }


                            /*if ((value/1000) > max_citytaxi_distance){

                                Toast.makeText(getActivity(),
                                        "You are not select city taxi for long distance",
                                        Toast.LENGTH_LONG).show();


                                if (key.equals("pickup")){

                                    globalClass.PICUP_ADDRESS = "";
                                    tv_pickup_address.setText("");

                                }else if (key.equals("drop")){

                                    globalClass.DROP_ADDRESS = "";
                                    tv_drop_address.setText("");
                                }

                            }*/

                        }


                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });


        }




    }




}
