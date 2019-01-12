package com.project.sketch.ugo.screen;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.project.sketch.ugo.R;
import com.project.sketch.ugo.httpRequest.apiModel3.BookHisSingle;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by developer on 10/1/19.
 */

public class HelpScreenActivity extends AppCompatActivity {
    TextView rider_name,dateTime,pickup,drop,tv_amt,tv_km,attach_data,attach_data_name,id,tv_status;
    EditText edt_description;
    BookHisSingle bookHisSingle;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;

    String Appconfig="http://apps.mars-west.sws.sketchwebsolutions.net/P-1163-UGO/api/user_help";
    ImageView image_delete;

    String TAG="help";
    File p_image;
    ProgressDialog pd;


    String desc;
    CircleImageView rider_image;
    LinearLayout ll_submit;
    private static final int PICKFILE_RESULT_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_screen);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(HelpScreenActivity.this,
                    Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(HelpScreenActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onCreate: ");
            }
            else{
                if(checkForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, 124)){
                    Log.d(TAG, "onCreate: ");
                }

            }
        }

        pd = new ProgressDialog(HelpScreenActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(getResources().getString(R.string.loading));
        bookHisSingle = (BookHisSingle) getIntent().getSerializableExtra("data");
        id=findViewById(R.id.id_);

        tv_status=findViewById(R.id.tv_status);
        pickup=findViewById(R.id.tv_pickup);

        attach_data=findViewById(R.id.attach_data);
        attach_data_name=findViewById(R.id.attach_data_name);
        rider_image=findViewById(R.id.imageView);
        edt_description=findViewById(R.id.edt_description);
        ll_submit=findViewById(R.id.ll_submit);
       // image_delete=findViewById(R.id.image_delete);
        setValues();
        attach_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();

            }
        });
        ll_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desc = edt_description.getText().toString();
              //  description = edt_description.getText().toString();

               // Log.d(TAG, "cat_id "+cat_id);

                postjob(desc);

            }
        });
/*
        image_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               attach_data_name.setText("");

            }
        });
*/


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            p_image = new File(getRealPathFromURI(uri));


            Log.d(TAG, "image = "+p_image);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
               // imageView2.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == PICK_IMAGE_CAMERA && resultCode == RESULT_OK) {


            File f = new File(Environment.getExternalStorageDirectory().toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }


            try {
                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();


                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                assert bitmap != null;
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

/*
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                        bitmapOptions);*/

                Log.d(TAG, "bitmap: "+bitmap);

               // imageView2.setImageBitmap(bitmap);

                String path = Environment.getExternalStorageDirectory()+File.separator;
                // + File.separator
                //   + "Phoenix" + File.separator + "default";
                f.delete();
                OutputStream outFile = null;
                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                try {

                    p_image = file;
                    attach_data_name.setText(file.toString());

                    outFile = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outFile);
                    outFile.flush();
                    outFile.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Bitmap photo = (Bitmap) data.getExtras().get("data");
            // iv_product_image.setImageBitmap(photo);
        }
      //  postjob();

    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public boolean checkForPermission(final String[] permissions, final int permRequestCode) {

        final List<String> permissionsNeeded = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            final String perm = permissions[i];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(HelpScreenActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {

                    Log.d("permisssion","not granted");


                    if (shouldShowRequestPermissionRationale(permissions[i])) {

                        Log.d("if","if");
                        permissionsNeeded.add(perm);

                    } else {
                        // add the request.
                        Log.d("else","else");
                        permissionsNeeded.add(perm);
                    }

                }
            }
        }

        if (permissionsNeeded.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // go ahead and request permissions
                requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]), permRequestCode);
            }
            return false;
        } else {
            // no permission need to be asked so all good...we have them all.
            return true;
        }

    }

    public void setValues(){


        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

       // rider_name.setText(bookHisSingle.getCustomer_name());
       // dateTime.setText(dateConversion(bookHisSingle.getTrip_start_time()));
        pickup.setText(bookHisSingle.getPick_address());
        tv_status.setText(bookHisSingle.getBooking_status());
        id.setText(bookHisSingle.getInvoice_number());
      //  drop.setText(bookHisSingle.getDrop_address());
    //    tv_amt.setText(bookHisSingle.getTotal_fare());
       // tv_km.setText(bookHisSingle.getBooking_km()+"km");



        Picasso.with(this)
                .load(bookHisSingle.getDriver_image())
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .resizeDimen(R.dimen.list_detail_image_size, R.dimen.list_detail_image_size)
                .centerInside()
                .tag(this)
                .into(rider_image);





      /*  mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                double lat1 = Double.parseDouble(booking.getPick_lat());
                double lng1 = Double.parseDouble(booking.getPick_lng());

              *//*  LatLng rider_pick_ll = new LatLng(lat1, lng1); ////your lat lng
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rider_pick_ll, 12.0f));
                googleMap.addMarker(new MarkerOptions()
                        .position(rider_pick_ll)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_icon)));*//*



                double lat2 = Double.parseDouble(booking.getDrop_lat());
                double lng2 = Double.parseDouble(booking.getDrop_lng());
              *//*  LatLng rider_drop_ll = new LatLng(lat2, lng2);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rider_drop_ll, 12.0f));
                googleMap.addMarker(new MarkerOptions()
                        .position(rider_drop_ll)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.driver_icon)));*//*

              Log.d("GC" , "val "+lat1+" "+ lng1+" "+ lat2+" "+ lng2);
                drawPolyline3(googleMap, lat1, lng1, lat2, lng2);
            }
        });*/
    }
    private String dateConversion(String date){
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss" , java.util.Locale.getDefault() );
        Date newDate= null;
        try {
            newDate = spf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        spf= new SimpleDateFormat("EEE, MMM dd, yyyy, hh:mm aa", java.util.Locale.getDefault());
        date = spf.format(newDate);
        return date;
    }
    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {
                        getResources().getString(R.string.take_photo),
                        getResources().getString(R.string.choose_from_gallery),
                        getResources().getString(R.string.cancel),
                };
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(HelpScreenActivity.this);
                builder.setTitle(getResources().getString(R.string.select_option));
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals(getResources().getString(R.string.take_photo))) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals(getResources().getString(R.string.choose_from_gallery))) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals(getResources().getString(R.string.cancel))) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(HelpScreenActivity.this, getResources().getString(R.string.camera_permission_error), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(HelpScreenActivity.this,  getResources().getString(R.string.camera_permission_error), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void postjob( final String description ){
        pd.show();
        String url = Appconfig;
        AsyncHttpClient cl = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("user_id",bookHisSingle.getUid());
        params.put("booking_id",bookHisSingle.getId());
        params.put("user_type","customer");
        params.put("comment",description);


        try{

            params.put("image", p_image);

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }


        Log.d(TAG , "filePath "+p_image);
        Log.d(TAG , "params "+params.toString());


        int DEFAULT_TIMEOUT = 30 * 1000;
        cl.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        cl.post(url,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                if (response != null) {
                    Log.d(TAG, "postjob- " + response.toString());
                    try {

                        //JSONObject result = response.getJSONObject("result");

                        int status = response.getInt("status");
                        String message = response.getString("message");
                        //int result = Integer.parseInt(success);

                        if(status==1) {

                            Intent i = new Intent(HelpScreenActivity.this, Container.class);
                            startActivity(i);
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }else{
                           // input_title.setText("");
                            edt_description.setText("");

                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();

                        // pd.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                // pd.dismiss();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Failed: ", ""+statusCode);
                Log.d("Error : ", "" + throwable);
            }
        });


    }

}
