package com.project.sketch.ugo.Payment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.project.sketch.ugo.R;
import com.project.sketch.ugo.screen.PaymentMode;
import com.project.sketch.ugo.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartPaymentActivity extends AppCompatActivity {

    String TAG="main";

    Pattern p = Pattern.compile("(\\{\\d+\\})");
   // Matcher m = p.matcher(payuResponse);
    PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
    //declare paymentParam object
    PayUmoneySdkInitializer.PaymentParam paymentParam = null;
    private ProgressDialog pDialog;
    SharedPref sharedPref;

    String txnid ="txt123468572", amount ="", phone ="9144040888",
            prodname ="Ugo amount", firstname ="", email ="",
            merchantId ="6575845", merchantkey="RxiQIa8D";  //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_startpayment);


        sharedPref = new SharedPref(this);
        pDialog  = new ProgressDialog(StartPaymentActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Please wait...");



        WebView mWebView = new WebView(this);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCacheMaxSize(100 * 1000 * 1000);
        mWebView.setWebChromeClient(new WebChromeClient());



        Intent intent = getIntent();
        phone = intent.getExtras().getString("phone");
        amount = intent.getExtras().getString("amount");
        email = intent.getExtras().getString("email");
        firstname = intent.getExtras().getString("name");

        startpay();
    }

    public void startpay(){

        builder.setAmount(amount)                          // Payment amount
                .setTxnId(txnid)                     // Transaction ID
                .setPhone(phone)                   // User Phone number
                .setProductName(prodname)                   // Product Name or description
                .setFirstName(firstname)// User First name
                .setEmail(email)              // User Email ID
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")     // Success URL (surl)
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")     //Failure URL (furl)
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(true)                              // Integration environment - true (Debug)/ false(Production)
                .setKey(merchantkey)                        // Merchant key
                .setMerchantId(merchantId);


        try {
            paymentParam = builder.build();
           // generateHashFromServer(paymentParam );
            getHashkey();

        } catch (Exception e) {
          Log.e(TAG, " error s "+e.toString());
        }

    }

    public void getHashkey(){
        ServiceWrapper service = new ServiceWrapper(null);
        Call<String> call = service.newHashCall(merchantkey, txnid, amount, prodname,
                firstname, email);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "hash res "+response.body());
                String merchantHash= response.body();

                Log.d(TAG, "merchantHash: "+merchantHash);

                if (merchantHash.isEmpty() || merchantHash.equals("")) {

                    Toast.makeText(StartPaymentActivity.this,
                            "Could not generate hash", Toast.LENGTH_SHORT).show();

                    Log.e(TAG, "hash empty");

                } else {
                    // mPaymentParams.setMerchantHash(merchantHash);
                    paymentParam.setMerchantHash(merchantHash);
                    // Invoke the following function to open the checkout page.
                    // PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, StartPaymentActivity.this,-1, true);
                    PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, StartPaymentActivity.this,
                            R.style.AppTheme_default, true);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "hash error "+ t.toString());
                Toast.makeText(getApplicationContext(), "Some error occurred",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " +data);
// PayUMoneySdk: Success -- payuResponse{"id":225642,"mode":"CC","status":"success","unmappedstatus":"captured","key":"9yrcMzso","txnid":"223013","transaction_fee":"20.00","amount":"20.00","cardCategory":"domestic","discount":"0.00","addedon":"2018-12-31 09:09:43","productinfo":"a2z shop","firstname":"kamal","email":"kamal.bunkar07@gmail.com","phone":"9144040888","hash":"b22172fcc0ab6dbc0a52925ebbd0297cca6793328a8dd1e61ef510b9545d9c851600fdbdc985960f803412c49e4faa56968b3e70c67fe62eaed7cecacdfdb5b3","field1":"562178","field2":"823386","field3":"2061","field4":"MC","field5":"167227964249","field6":"00","field7":"0","field8":"3DS","field9":" Verification of Secure Hash Failed: E700 -- Approved -- Transaction Successful -- Unable to be determined--E000","payment_source":"payu","PG_TYPE":"AXISPG","bank_ref_no":"562178","ibibo_code":"VISA","error_code":"E000","Error_Message":"No Error","name_on_card":"payu","card_no":"401200XXXXXX1112","is_seamless":1,"surl":"https://www.payumoney.com/sandbox/payment/postBackParam.do","furl":"https://www.payumoney.com/sandbox/payment/postBackParam.do"}
//PayUMoneySdk: Success -- merchantResponse438104
// on successfull txn
      //  request code 10000 resultcode -1
      //tran {"status":0,"message":"payment status for :438104","result":{"postBackParamId":292490,"mihpayid":"225642","paymentId":438104,"mode":"CC","status":"success","unmappedstatus":"captured","key":"9yrcMzso","txnid":"txt12345","amount":"20.00","additionalCharges":"","addedon":"2018-12-31 09:09:43","createdOn":1546227592000,"productinfo":"a2z shop","firstname":"kamal","lastname":"","address1":"","address2":"","city":"","state":"","country":"","zipcode":"","email":"kamal.bunkar07@gmail.com","phone":"9144040888","udf1":"","udf2":"","udf3":"","udf4":"","udf5":"","udf6":"","udf7":"","udf8":"","udf9":"","udf10":"","hash":"0e285d3a1166a1c51b72670ecfc8569645b133611988ad0b9c03df4bf73e6adcca799a3844cd279e934fed7325abc6c7b45b9c57bb15047eb9607fff41b5960e","field1":"562178","field2":"823386","field3":"2061","field4":"MC","field5":"167227964249","field6":"00","field7":"0","field8":"3DS","field9":" Verification of Secure Hash Failed: E700 -- Approved -- Transaction Successful -- Unable to be determined--E000","bank_ref_num":"562178","bankcode":"VISA","error":"E000","error_Message":"No Error","cardToken":"","offer_key":"","offer_type":"","offer_availed":"","pg_ref_no":"","offer_failure_reason":"","name_on_card":"payu","cardnum":"401200XXXXXX1112","cardhash":"This field is no longer supported in postback params.","card_type":"","card_merchant_param":null,"version":"","postUrl":"https:\/\/www.payumoney.com\/mobileapp\/payumoney\/success.php","calledStatus":false,"additional_param":"","amount_split":"{\"PAYU\":\"20.0\"}","discount":"0.00","net_amount_debit":"20","fetchAPI":null,"paisa_mecode":"","meCode":"{\"vpc_Merchant\":\"TESTIBIBOWEB\"}","payuMoneyId":"438104","encryptedPaymentId":null,"id":null,"surl":null,"furl":null,"baseUrl":null,"retryCount":0,"merchantid":null,"payment_source":null,"pg_TYPE":"AXISPG"},"errorCode":null,"responseCode":null}---438104

        // Result Code is -1 send from Payumoney activity
        Log.e("data_new",""+data);
        Log.e("StartPaymentActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse =
                    data.getParcelableExtra( PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE );

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if(transactionResponse.getTransactionStatus()
                        .equals( TransactionResponse.TransactionStatus.SUCCESSFUL )){
                    //Success Transaction

                } else{
                    //Failure Transaction
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();
              //  String new_pay=m.group();
             //   String val1 = m.group().replace("{", "").replace("}", "");
                Log.d(TAG, "payuResponse: "+payuResponse);
              //  Log.d(TAG, "val1: "+val1);


                try {


                    JSONObject jsonObject = new JSONObject(payuResponse);
                     String Details=jsonObject.toString();
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");
                    Log.d(TAG, "status message : " +status+message);
                    JSONObject jsonObj = jsonObject.getJSONObject("result");
                    int postBackParamId = Integer.parseInt(jsonObj.getString("postBackParamId"));
                    String mihpayid = jsonObj.getString("mihpayid");
                    String paymentId = jsonObj.getString("paymentId");
                    String paymentstatus = jsonObj.getString("status");
                    String key = jsonObj.getString("key");
                    String txnid = jsonObj.getString("txnid");
                    String amount = jsonObj.getString("amount");
                    String date = jsonObj.getString("addedon");
                    String productinfo = jsonObj.getString("productinfo");
                    String firstname = jsonObj.getString("firstname");
                    String email = jsonObj.getString("email");
                    String phone = jsonObj.getString("phone");
                    String hash = jsonObj.getString("hash");
                    String bankcode = jsonObj.getString("bankcode");
                    String name_on_card = jsonObj.getString("name_on_card");
                    String cardnum = jsonObj.getString("cardnum");
                    String payuMoneyId = jsonObj.getString("payuMoneyId");


                    checkWallet(Details,status,message,postBackParamId,mihpayid,paymentId,paymentstatus,key,
                                txnid,amount,date,productinfo,firstname,email,phone,hash,bankcode,name_on_card,
                                cardnum,payuMoneyId);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();

            } /* else if (resultModel != null && resultModel.getError() != null) {
                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d(TAG, "Both objects are null!");
            }*/
        }else {

            finish();
        }
    }
    public void checkWallet(final String Details, final String status, final String message, final int postBackParamId,
                            final String mihpayid,final String paymentId,final String paymentstatus,final String key,final String txnid,
                            final String amount,final String date,final String productinfo,final String firstname,
                            final String email,final String phone,final String hash,final String bankcode,final String name_on_card,final String cardnum,final String payuMoneyId){

        pDialog.show();

        String url ="http://u-go.in/api/save_payment";
        AsyncHttpClient cl = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("user_id", sharedPref.getUserId());
        params.put("Details", Details);
        params.put("status", status);
        params.put("message", message);
        params.put("postBackParamId", postBackParamId);
        params.put("mihpayid", mihpayid);
        params.put("paymentId", paymentId);
        params.put("paymentstatus", paymentstatus);
        params.put("key", key);
        params.put("txnid", txnid);
        params.put("amount", amount);
        params.put("date",date);
        params.put("productinfo",productinfo);
        params.put("firstname",firstname);

      //  params.put("address1",  address1);
        params.put("email",  email);
        params.put("phone",  phone);
        params.put("hash",  hash);
        params.put("bankcode",  bankcode);
        params.put("name_on_card",  name_on_card);
        params.put("cardnum",  cardnum);
        params.put("payuMoneyId",  payuMoneyId);

        Log.d(TAG , "URL "+url);
        Log.d(TAG , "params "+params.toString());


        int DEFAULT_TIMEOUT = 30 * 1000;
        cl.setMaxRetriesAndTimeout(5 , DEFAULT_TIMEOUT);
        cl.post(url,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "user_main_category_url- " + response.toString());
                if (response != null) {
                    Log.d(TAG, "user_main_category_url- " + response.toString());
                    try {



                        int status = response.getInt("status");
                        String message = response.getString("message");

                        if (status == 1) {

                            pDialog.dismiss();
                            String wallet_amount = response.getString("wallet_amount");
                            // float new_amt = Float.parseFloat(wallet_amount);
                            Intent payment=new Intent(getApplicationContext(), PaymentMode.class);
                           // payment.putExtra("wallet_amount",wallet_amount);
                            payment.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            payment.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                            startActivity(payment);
                           // tv_wallet_amount.setText(wallet_amount);


                        }


                        else{


                            Toast.makeText(StartPaymentActivity.this, ""+message, Toast.LENGTH_LONG).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }



            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d(TAG, "responseString- " + responseString.toString());
                Log.d("Failed: ", ""+statusCode);
                Log.d("Error : ", "" + throwable);
            }
        });


    }


}
