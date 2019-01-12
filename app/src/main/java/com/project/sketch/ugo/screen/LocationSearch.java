package com.project.sketch.ugo.screen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.project.sketch.ugo.R;
import com.project.sketch.ugo.adapter.FavAddressListAdapter;
import com.project.sketch.ugo.database.DatabaseHelper;
import com.project.sketch.ugo.database.FavDataModel;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.GlobalClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.project.sketch.ugo.utils.Constants.API_KEY;

/**
 * Created by Developer on 1/11/18.
 */

public class LocationSearch extends AppCompatActivity implements
        AdapterView.OnItemClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        FavAddressListAdapter.ViewClickListener{



    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

   // private static final String API_KEY = "AIzaSyDdh5Q1wVZ_XJHAQTTVc0DVJbMQ1B8BWEU";



    GlobalClass global;
    DatabaseHelper databaseHelper;

    String Key;

    Toolbar toolbar;
    AutoCompleteTextView autoCompView;
    RecyclerView recyclerview_fav;
    ImageView remove;

    LinearLayoutManager layoutManager;
    FavAddressListAdapter adapter;

    ArrayList<FavDataModel> dataModelArrayList;

    String selected_address;


    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private GoogleApiClient mGoogleApiClient;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_search_screen);


        autoCompView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        recyclerview_fav = (RecyclerView) findViewById(R.id.recyclerview_fav);
        layoutManager = new LinearLayoutManager(this);
        remove = (ImageView) findViewById(R.id.remove);
        remove.setVisibility(View.INVISIBLE);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Location");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_arrow_back_black_24dp);

        global = (GlobalClass)getApplicationContext();


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Key = extras.getString("key", "");
        }




        autoCompView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() <= 0){
                    remove.setVisibility(View.INVISIBLE);
                }else {
                    remove.setVisibility(View.VISIBLE);
                }

            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompView.setText("");
            }
        });


        autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this));
        autoCompView.setOnItemClickListener(this);


        listFavouriteAddress();


        ///////////////////////////////////////////////////////////////


        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();



        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);


        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                //.setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("IN")
                .build();

        autocompleteFragment.setFilter(typeFilter);


        /*try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(typeFilter)
                    .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }*/

        autocompleteFragment.setHint("Search your address");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.d(Constants.TAG, "Place:1 " + place.getName());
                Log.d(Constants.TAG, "Address:1 " + place.getAddress());

                selected_address = place.getName() + ", "+place.getAddress();

                GetLatLng(selected_address);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.d(Constants.TAG, "An error occurred: " + status);
            }

        });




    }

    public void onItemClick(AdapterView adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);


        Log.d(Constants.TAG, ""+str);

    }

    public static ArrayList autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:ind");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {

            //   Log.e(LOG_TAG, "Error processing Places API URL", e);

            return resultList;
        } catch (IOException e) {
            //   Log.e(LOG_TAG, "Error connecting to Places API", e);

            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
            System.out.println(predsJsonArray.toString());

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {

            //   Log.e(LOG_TAG, "Cannot process JSON results", e);

        }

        return resultList;
    }



    class GooglePlacesAutocompleteAdapter extends BaseAdapter implements Filterable {
        private ArrayList<String> resultList = new ArrayList<>();
        private LayoutInflater inflater = null;
        private Context _context;
        private GooglePlacesAutocompleteAdapter.ViewHolder viewHolder;


        public GooglePlacesAutocompleteAdapter(Context context) {
            this._context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            ImageView iv_icon;
            TextView tv_address;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.location_search_item, parent, false);
                viewHolder = new GooglePlacesAutocompleteAdapter.ViewHolder();

                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);


                convertView.setTag(viewHolder);

            }else {
                viewHolder = (GooglePlacesAutocompleteAdapter.ViewHolder) convertView.getTag();
            }


            viewHolder.tv_address.setText(resultList.get(position));

            return convertView;
        }



        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu  ) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // getMenuInflater().inflate(R.menu.menu_marchant_page, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:

                overridePendingTransition(R.anim.stay, R.anim.top_to_bottom);
                finish();

                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.stay, R.anim.top_to_bottom);
        finish();
        super.onBackPressed();

    }




    private void listFavouriteAddress(){

        databaseHelper = new DatabaseHelper(this);

        dataModelArrayList = databaseHelper.getFavAddress();

        adapter = new FavAddressListAdapter(this, dataModelArrayList);
        recyclerview_fav.setAdapter(adapter);
        recyclerview_fav.setLayoutManager(layoutManager);


        adapter.setViewClickListener(this);

    }

    @Override
    public void onImageClicked(int position) {

        FavDataModel favDataModel = dataModelArrayList.get(position);

        double lat = Double.parseDouble(favDataModel.getLATITUDE());
        double lng = Double.parseDouble(favDataModel.getLONGITUDE());

        if (Key.equals("pickup")){

            global.PICUP_ADDRESS = favDataModel.getADDRESS();
            global.PICKUP_LATLNG = new LatLng(lat, lng);

            Log.d(Constants.TAG, "global.PICKUP_LATLNG > "+ global.PICKUP_LATLNG);

        }else if (Key.equals("drop")){

            global.DROP_ADDRESS = favDataModel.getADDRESS();
            global.DROP_LATLNG = new LatLng(lat, lng);

            Log.d(Constants.TAG, "global.DROP_LATLNG > "+ global.DROP_LATLNG);
        }


        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(autoCompView.getWindowToken(), 0);


        Intent returnIntent = new Intent();
        returnIntent.putExtra("key", Key);
        setResult(Activity.RESULT_OK,returnIntent);
        overridePendingTransition(R.anim.stay, R.anim.top_to_bottom);
        finish();

    }


    ////////////////////////////////////////////////////////////////


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(Constants.TAG, ""+connectionResult);

    }


    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.d(Constants.TAG, "Place:2 " + place.getName());
                Log.d(Constants.TAG, "Address:2 " + place.getAddress());

                selected_address = place.getName() + ", "+place.getAddress();

               // GetLatlngFromAddress(LocationSearch.this, place.getAddress().toString());

                GetLatLng(selected_address);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.d(Constants.TAG, "RESULT_ERROR = "+status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        mGoogleApiClient.connect();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }


    ///////////////////////////////////////////////



    public void GetLatLng(final String str){

        final ProgressDialog dialog = new ProgressDialog(LocationSearch.this);
        dialog.setMessage("Fetching Address...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        String URL = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + str + "&key=" + API_KEY;

        Log.d(Constants.TAG, "URL - "+URL);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //Here response will be received in form of JSONObject
                Log.i(Constants.TAG,"Server response - "+response );

                try {

                    double lng = ((JSONArray)response.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");

                    double lat = ((JSONArray)response.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");

                    Log.d("latitude", "" + lat);
                    Log.d("longitude", "" + lng);


                    String locality = ((JSONArray)response.get("results")).getJSONObject(0)
                            .getJSONArray("address_components").getJSONObject(1)
                            .getString("long_name");



                    LatLng latLng = new LatLng(lat, lng);

                    if (Key.equals("pickup")){

                        global.PICUP_ADDRESS = str;
                        global.PICKUP_LATLNG = latLng;
                        global.Pickup_City = locality;


                        Log.d(Constants.TAG, "global.PICKUP_LATLNG > "+ global.PICKUP_LATLNG);
                        Log.d(Constants.TAG, "city > "+locality);

                    }else if (Key.equals("drop")){

                        global.DROP_ADDRESS = str;
                        global.DROP_LATLNG = latLng;
                        global.DropOff_City = locality;

                        Log.d(Constants.TAG, "global.DROP_LATLNG > "+ global.DROP_LATLNG);
                        Log.d(Constants.TAG, "city > "+locality);
                    }


                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(autoCompView.getWindowToken(), 0);

                    Log.d(Constants.TAG, ">>>>>>>>>>>>>>>>>>");
                    Log.d(Constants.TAG, ">>>>>>>>>>>>>>>>>>");


                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("key", Key);
                    setResult(Activity.RESULT_OK,returnIntent);
                    overridePendingTransition(R.anim.stay, R.anim.top_to_bottom);
                    finish();


                }catch (Exception e){
                    e.printStackTrace();
                }

                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                dialog.dismiss();
            }
        });

    }





}
