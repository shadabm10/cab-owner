package com.project.sketch.ugo.screen;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.sketch.ugo.R;
import com.project.sketch.ugo.adapter.SlidingImage_Adapter;
import com.project.sketch.ugo.httpRequest.ApiClient;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.GlobalClass;
import com.project.sketch.ugo.utils.SharedPref;
import com.rampo.updatechecker.UpdateChecker;
import com.rampo.updatechecker.notice.Notice;
import com.splunk.mint.Mint;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;

/**
 * Created by Developer on 1/8/18.
 */

public class Splash extends AppCompatActivity {

    private static ViewPager mPager;
    CirclePageIndicator indicator;
    RelativeLayout rl_registration,rl_login;
    ProgressDialog pDialog;


    private ArrayList<Integer> ImagesArray;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

    final Handler handler = new Handler();
    Timer swipeTimer = new Timer();
    GlobalClass globalClass;

    private ArrayList<Integer> ImArray;

    SharedPref sharedPref;
    public static Activity fa;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        globalClass=(GlobalClass)getApplicationContext();
        sharedPref = new SharedPref(this);

        Mint.initAndStartSession(this.getApplication(), "1cba8ec9");

        if (sharedPref.idFirstLogin()){

            isLoginCheckApi();

        }else {

            setContentView(R.layout.splash_screen);
            initViews();

            fa = this;

        }

        UpdateChecker checker = new UpdateChecker(this);
        checker.setNotice(Notice.NOTIFICATION);
        checker.start();

        requestPermission();





    }
   /* @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }*/
    public void initViews(){

        mPager = (ViewPager) findViewById(R.id.viewpager);
        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        rl_registration = (RelativeLayout)findViewById(R.id.rl_registration);
        rl_login = (RelativeLayout)findViewById(R.id.rl_login);


        ImagesArray = new ArrayList<>();
        ImArray = new ArrayList<>();

        ImagesArray.add(R.mipmap.splash_bg);
        ImagesArray.add(R.mipmap.splash_bg);
        ImagesArray.add(R.mipmap.splash_bg);
        ImagesArray.add(R.mipmap.splash_bg);

        //initViewpager();



        rl_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Splash.this, Registration.class);
                startActivity(intent);


            }
        });


        rl_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sharedPref.idFirstLogin()){

                    Intent intent = new Intent(Splash.this, Container.class);
                    startActivity(intent);

                }else {

                    Intent intent = new Intent(Splash.this, Login.class);
                    startActivity(intent);
                }


            }
        });




    }


    private void initViewpager() {
        for (int i = 0; i < ImagesArray.size(); i++)
            ImArray.add(ImagesArray.get(i));


        mPager.setAdapter(new SlidingImage_Adapter(Splash.this, ImArray));


        indicator.setViewPager(mPager);

        final float density = getResources().getDisplayMetrics().density;

        //Set circle_green indicator radius
        indicator.setRadius(4 * density);

        NUM_PAGES = ImArray.size();

        // Auto start of viewpager

        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };

        handler.removeCallbacks(Update);



        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);

        handler.postDelayed(Update,10000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });


    }

    private static final int PERMISSION_REQUEST_CODE = 1;

    private void requestPermission(){

        ActivityCompat.requestPermissions((Activity)Splash.this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("UGO", "PERMISSION_GRANTED >>>>");


                    UpdateChecker checker = new UpdateChecker(this);
                    checker.start();

                } else {
                    //code for deny
                }
                break;
        }
    }

    public SSLContext getSslContext() {

        TrustManager[] byPassTrustManagers = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        } };

        SSLContext sslContext=null;

        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext;
    }


    public void isLoginCheckApi(){

        final String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Checking...");
        pDialog.show();


        String url = ApiClient.BASE_URL + Constants.check_login;
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        client.setSSLSocketFactory(
                new SSLSocketFactory(getSslContext(),
                        SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));

        params.put(Constants.id, sharedPref.getUserId());

        Log.d(Constants.TAG , "check_login - " + url);
        Log.d(Constants.TAG , "check_login - " + params.toString());

        int DEFAULT_TIMEOUT = 30 * 1000;
        client.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(Constants.TAG, "check_login- " + response.toString());

                if (response != null) {
                    try {

                        int status = response.optInt("status");
                        String message = response.optString("message");

                        if (status == 1){

                            JSONObject data = response.getJSONObject("data");

                            String login_status = data.getString("login_status");
                            String device_id = data.getString("device_id");

                            if (login_status.equals("Yes") &&
                                    android_id.equals(device_id)){


                                Intent intent = new Intent(Splash.this,
                                        LocationPermission.class);
                                startActivity(intent);
                                finish();

                            }else {

                                setContentView(R.layout.splash_screen);
                                initViews();
                                fa = Splash.this;
                            }


                        }else {

                          //  Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                            setContentView(R.layout.splash_screen);
                            initViews();
                            fa = Splash.this;
                        }

                        pDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                pDialog.dismiss();
            }

        });


    }

}
