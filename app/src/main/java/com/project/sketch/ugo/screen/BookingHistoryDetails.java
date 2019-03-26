package com.project.sketch.ugo.screen;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.httpRequest.ApiInterface;
import com.project.sketch.ugo.httpRequest.apiModel2.Cab;
import com.project.sketch.ugo.httpRequest.apiModel3.BookHisSingle;
import com.project.sketch.ugo.httpRequest.apiModel4.FeedbackRatingResponse;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.GlobalClass;
import com.project.sketch.ugo.utils.MapUtils;
import com.project.sketch.ugo.utils.SharedPref;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;


/**
 * Created by ANDROID on 1/10/2018.
 */

public class BookingHistoryDetails extends AppCompatActivity implements OnMapReadyCallback {

    Toolbar toolbar;
    SupportMapFragment mapFrag;
    BookHisSingle bookHisSingle;
    private GoogleMap map;
    GlobalClass globalClass;
    CircleImageView imageView;
    TextView tv_invoiceNo, tv_driver_name, tv_car_name, tv_car_number, tv_status, tv_by_me;
    TextView tv_pickup_time, tv_pickup_address, tv_drop_time, tv_drop_address;
    TextView tv_ride_fare, tv_guide_fees, tv_tax, tv_total, tv_payment_mode, tv_coupon_amt,tv_waiting_fees;
    TextView tv_roundOff, tv_g_total, tv_total_distance, tv_total_ride_time;
    RatingBar rating_driver;
    float waiting_fee ;
    float gst_amt ;
    float total_new ;
    RelativeLayout rl1, rl_31, rl_coupon;

    LayerDrawable stars;
    float subtotal;
    float commission;
    double grand_total;
    SharedPref sharedPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_booking_details);
        initViews();

    }
    @Override
    protected void onResume() {
        super.onResume();

      //  showRatingDialog();
    }
    public void initViews(){
        globalClass = (GlobalClass) getApplicationContext();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        mapFrag = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map3);
        mapFrag.getMapAsync(this);

        sharedPref = new SharedPref(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_details));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);

        imageView = (CircleImageView) findViewById(R.id.imageView);
        rating_driver = (RatingBar) findViewById(R.id.rating_driver);
        tv_invoiceNo = (TextView) findViewById(R.id.tv_invoiceNo);
        tv_waiting_fees = (TextView) findViewById(R.id.tv_waiting_fees);
        tv_driver_name = (TextView) findViewById(R.id.tv_driver_name);
        tv_car_name = (TextView) findViewById(R.id.tv_car_name);
        tv_car_number = (TextView) findViewById(R.id.tv_car_number);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_by_me = (TextView) findViewById(R.id.tv_by_me);
        tv_pickup_time = (TextView) findViewById(R.id.tv_pickup_time);
        tv_pickup_address = (TextView) findViewById(R.id.tv_pickup_address);
        tv_drop_time = (TextView) findViewById(R.id.tv_drop_time);
        tv_drop_address = (TextView) findViewById(R.id.tv_drop_address);
        tv_ride_fare = (TextView) findViewById(R.id.tv_ride_fare);
        tv_guide_fees = (TextView) findViewById(R.id.tv_guide_fees);
        tv_tax = (TextView) findViewById(R.id.tv_tax);
        tv_guide_fees = (TextView) findViewById(R.id.tv_guide_fees);
        tv_total = (TextView) findViewById(R.id.tv_total);
        tv_payment_mode = (TextView) findViewById(R.id.tv_payment_mode);
       // tv_roundOff = (TextView) findViewById(R.id.tv_roundOff);
       // tv_g_total = (TextView) findViewById(R.id.tv_g_total);
        tv_coupon_amt = (TextView) findViewById(R.id.tv_coupon_amt);
        tv_total_distance = (TextView) findViewById(R.id.tv_total_distance);
        tv_total_ride_time = (TextView) findViewById(R.id.tv_total_ride_time);

        rl1 = (RelativeLayout) findViewById(R.id.rl1);
        rl1.setVisibility(View.GONE);
        rl_31 = (RelativeLayout) findViewById(R.id.rl_31);
        rl_coupon = (RelativeLayout) findViewById(R.id.rl_311);
        rl_coupon.setVisibility(View.GONE);


        tv_by_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null){

            bookHisSingle = (BookHisSingle) bundle.getSerializable("data");

            Picasso.with(this).load(bookHisSingle.getDriver_image()).error(R.mipmap.avatar).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    //  Log.d("TAG", "onSuccess");
                }
                @Override
                public void onError() {
                    //  Toast.makeText(mactivity, "An error occurred", Toast.LENGTH_SHORT).show();
                }
            });


            tv_invoiceNo.setText("Invoice No : "+bookHisSingle.getInvoice_number());
            tv_driver_name.setText(bookHisSingle.getDriver_name());
            tv_car_name.setText(bookHisSingle.getCab_name() +" - " + bookHisSingle.getCar_name());
            tv_car_number.setText(bookHisSingle.getCar_number());

            tv_pickup_time.setText(getTimeOnly(bookHisSingle.getTrip_start_time()));
            tv_pickup_address.setText(bookHisSingle.getPick_address());

            tv_drop_time.setText(getTimeOnly(bookHisSingle.getTrip_end_time()));
            tv_drop_address.setText(bookHisSingle.getDrop_address());

            tv_total_distance.setText("Distance : "+bookHisSingle.getBooking_km()+"Km.");
            tv_total_ride_time.setText("Total time : "
                    +secondMinHour(Integer.parseInt(bookHisSingle.getBooking_total_time())));


            tv_payment_mode.setText("Payment Mode : "+bookHisSingle.getPayment_mode());


            if (Float.parseFloat(bookHisSingle.getDriver_rating_by_me()) > 0){

                stars = (LayerDrawable) rating_driver.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(Color.parseColor("#FFA100"), PorterDuff.Mode.SRC_ATOP); // for filled stars
                stars.getDrawable(1).setColorFilter(Color.parseColor("#989898"), PorterDuff.Mode.SRC_ATOP); // for half filled stars
                stars.getDrawable(0).setColorFilter(Color.parseColor("#989898"), PorterDuff.Mode.SRC_ATOP); // for empty stars
                rating_driver.setRating(Float.parseFloat(bookHisSingle.getDriver_rating_by_me()));

                tv_by_me.setText("- Rated by me");
                tv_by_me.setOnClickListener(null);

            }else {

                rating_driver.setVisibility(View.INVISIBLE);

                tv_by_me.setTextColor(getResources().getColor(R.color.status_blue));
                tv_by_me.setClickable(true);

                String text = "Rate driver";
                SpannableString content = new SpannableString(text);
                content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
                tv_by_me.setText(content);
            }


            setStatus(bookHisSingle.getBooking_status());

            getSupportActionBar().setTitle(getNewFormatDate(bookHisSingle.getCreated_date()));

             subtotal = Float.parseFloat(bookHisSingle.getSub_total_amt());
             commission = Float.parseFloat(bookHisSingle.getCommission_amt());
             grand_total = subtotal + commission;

            tv_ride_fare.setText("₹ "+df.format(grand_total));
            tv_guide_fees.setText("₹ "+bookHisSingle.getGuide_charges());
            tv_coupon_amt.setText("₹ "+df.format(bookHisSingle.getCoupon_amount()));



            try{


                double total = 0.0;
                double guide_fee = 0.0;

                Log.d(Constants.TAG, "Guide = "+bookHisSingle.getGuide());

                total = Double.parseDouble(bookHisSingle.getTotal_fare());

                if (bookHisSingle.getGuide().matches("Y")){
                    rl_31.setVisibility(View.VISIBLE);
                    guide_fee = Double.parseDouble(bookHisSingle.getGuide_charges());
                }else {
                    rl_31.setVisibility(View.GONE);
                }

                if (bookHisSingle.getCoupon_applied().matches("Y")){
                    rl_coupon.setVisibility(View.VISIBLE);
                    total = total - bookHisSingle.getCoupon_amount();
                }else {
                    rl_coupon.setVisibility(View.GONE);
                }


                 waiting_fee = Float.parseFloat(bookHisSingle.getWaiting_price());
                 gst_amt = Float.parseFloat(bookHisSingle.getGst_amt());
                 total_new = Float.parseFloat(bookHisSingle.getTotal_fare());

               // double tax_amount = total * (gst_rate);

                tv_tax.setText("₹ "+df.format(gst_amt));
                tv_waiting_fees.setText("₹ "+df.format(waiting_fee));



                tv_total.setText("₹ "+df.format(total_new));

               // calculateRoundOff(String.valueOf(df.format(grand_total)));

               // tv_g_total.setText("₹ "+df.format(Math.round(grand_total)));


                rl1.setVisibility(View.VISIBLE);


            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    ///////////  Calculate round OFF ..........

    String RoundOffAmount;

    public void calculateRoundOff(String grand_total){

        String lastTwo = null;
        if (grand_total != null && grand_total.length() >= 2) {
            lastTwo = grand_total.substring(grand_total.length() - 2);
        }
        //  Log.d(global.TAG, "lastTwo = "+lastTwo);

        if (Integer.parseInt(lastTwo) == 00){

            RoundOffAmount = "0.00";

        }else {

            if (Integer.parseInt(lastTwo) < 50){

                int round_off = Integer.parseInt(lastTwo);
                RoundOffAmount = "- 0."+lastTwo;

            } else if (Integer.parseInt(lastTwo) > 90){

                int round_off = 100 - Integer.parseInt(lastTwo);
                RoundOffAmount = "+ 0.0"+String.valueOf(round_off);

            } else if (Integer.parseInt(lastTwo) >= 50 && Integer.parseInt(lastTwo) <= 90) {

                int round_off = 100 - Integer.parseInt(lastTwo);
                RoundOffAmount = "+ 0."+String.valueOf(round_off);

            }

        }

        tv_roundOff.setText(RoundOffAmount);
    }

    public void setStatus(String status){

        switch (status){
            case "completed" :
                tv_status.setText("Completed");
                tv_status.setTextColor(getResources().getColor(R.color.status_green));

                break;
            case "cancelled" :
                tv_status.setText("Cancelled");
                tv_status.setTextColor(getResources().getColor(R.color.status_red));

                break;

            default:
                tv_status.setText("");

                break;
        }

    }

    public String getTimeOnly(String datetime){
        String time = "";
        try {
            //2018-02-14 11:11:49

            String dateStr = datetime;

            DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = srcDf.parse(dateStr);

            DateFormat destDf = new SimpleDateFormat("hh:mm a");

            // format the date into another format
            time = destDf.format(date);

            System.out.println("Converted time is : " + time);

        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    public String getNewFormatDate(String datetime){
        String time = "";
        try {
            //2018-02-14 11:11:49

            String dateStr = datetime;

            DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = srcDf.parse(dateStr);

            DateFormat destDf = new SimpleDateFormat("EEE dd-MMM-yyyy hh:mm a");

            // format the date into another format
            time = destDf.format(date);

            System.out.println("Converted time is : " + time);

        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    private String secondMinHour(int i){
        long hours = i / 3600;
        long minutes = (i % 3600) / 60;
        long seconds = i % 60;
        String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return time;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:

                finish();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }


    @Override
    public void onMapReady(GoogleMap mapp) {

        map = mapp;

        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setScrollGesturesEnabled(false);


        map.setTrafficEnabled(false);
        map.setIndoorEnabled(false);
        map.setBuildingsEnabled(false);


        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        //  double lat1 = 22.567438;
        //  double long1 = 88.375214;

        try{

            double pick_lan = Double.parseDouble(bookHisSingle.getPick_lat());
            double pick_long = Double.parseDouble(bookHisSingle.getPick_lng());
            LatLng pick_latlong = new LatLng(pick_lan, pick_long);


            double drop_lan = Double.parseDouble(bookHisSingle.getDrop_lat());
            double drop_long = Double.parseDouble(bookHisSingle.getDrop_lng());
            LatLng drop_latlong = new LatLng(drop_lan, drop_long);



            MarkerOptions markerOptions1 = new MarkerOptions();
            markerOptions1.position(pick_latlong);

            markerOptions1.icon(BitmapDescriptorFactory.fromBitmap(globalClass.cab_image));
            map.addMarker(markerOptions1);

            map.moveCamera(CameraUpdateFactory.newLatLng(pick_latlong));
            map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(mapp.getCameraPosition().target)
                    .zoom(16)
                    .bearing(30)
                    .tilt(45)
                    .build()));


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    map.setMyLocationEnabled(false);
                }
            }
            else {

                map.setMyLocationEnabled(false);
            }

            MapUtils mapUtils = new MapUtils(BookingHistoryDetails.this);
            mapUtils.drawPolyline3(map, pick_latlong.latitude, pick_latlong.longitude, drop_latlong.latitude, drop_latlong.longitude);


        }catch (Exception e){
            e.printStackTrace();
        }




        //getSnapShot();
    }


    ////////////////////////////////////////////////////////////
    /// Rating Dialog ....
    float rating_ = 0;

    public void showRatingDialog(){
        rating_ = 0;
        DecimalFormat df = new DecimalFormat();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.rating_dialog, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        RatingBar rating_driver =  dialogView.findViewById(R.id.rating_driver);
        stars = (LayerDrawable) rating_driver.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#FFA100"), PorterDuff.Mode.SRC_ATOP); // for filled stars
        stars.getDrawable(1).setColorFilter(Color.parseColor("#989898"), PorterDuff.Mode.SRC_ATOP); // for half filled stars
        stars.getDrawable(0).setColorFilter(Color.parseColor("#989898"), PorterDuff.Mode.SRC_ATOP); // for empty stars

        final EditText edt_comment =  dialogView.findViewById(R.id.edt_comment);
        Button btn_submit =  dialogView.findViewById(R.id.btn_submit);
        //TextView tv_fare =  dialogView.findViewById(R.id.tv_fare);


        rating_driver.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating_ = rating;
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postFeedback(String.valueOf(rating_), edt_comment.getText().toString());

                alertDialog.dismiss();

            }
        });

    }

    private void postFeedback(final String rating, String comment){

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<FeedbackRatingResponse> call = apiService.feedbackRating(
                sharedPref.getUserId(),
                bookHisSingle.getId(),
                rating,
                comment
        );

        call.enqueue(new retrofit2.Callback<FeedbackRatingResponse>() {
            @Override
            public void onResponse(Call<FeedbackRatingResponse>call, retrofit2.Response<FeedbackRatingResponse> response) {

                Log.d(Constants.TAG, "onResponse: " + response.body().getMessage());
                Log.d(Constants.TAG, "onResponse: " + response.body().getStatus());

                if (response.body().getStatus() == 1){

                    Toast.makeText(BookingHistoryDetails.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                    rating_driver.setRating(Float.parseFloat(rating));

                    tv_by_me = (TextView) findViewById(R.id.tv_by_me);
                    tv_by_me.setText("- Rated by me");
                    tv_by_me.setOnClickListener(null);

                }else if (response.body().getStatus() == 2){

                    Toast.makeText(BookingHistoryDetails.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                }else {

                    Toast.makeText(BookingHistoryDetails.this, ""+response.body().getMessage(), Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onFailure(Call<FeedbackRatingResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(Constants.TAG, t.toString());

            }
        });

    }

    ////////////////////////////////////////////////////////////



    public void getSnapShot(){

       /* GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {

                ImageView iv_mapp = (ImageView) findViewById(R.id.iv_mapp);

                iv_mapp.setImageBitmap(bitmap);
            }
        };


        map.snapshot(callback);*/


    }

}
