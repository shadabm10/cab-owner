package com.project.sketch.ugo.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;
import com.project.sketch.ugo.R;

/**
 * Created by Developer on 1/16/18.
 */

public class ValidationClass {

    private Context context;
    private String android_id;
    private String fcm_token;

    public ValidationClass(Context context) {
        this.context = context;

        android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        fcm_token = FirebaseInstanceId.getInstance().getToken();
    }

    public String getAndroid_id() {
        return android_id;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    //////////////////////////////////////////////


    public boolean validateName(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.requestFocus();
            editText.setError(context.getResources().getString(R.string.msg_enter_name));
            return false;
        } else {

        }

        return true;
    }


    public boolean validateEmail(EditText editText) {
        String email = editText.getText().toString().trim();

        if (email.isEmpty()) {
            editText.requestFocus();
            editText.setError(context.getResources().getString(R.string.msg_enter_email));
            return false;
        } else {

            if (!isValidEmail(email)){
                editText.setError(context.getResources().getString(R.string.msg_enter_valid_email));
                editText.requestFocus();
                return false;

            }else {

            }

        }

        return true;
    }

    public boolean validateMobileNo(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError(context.getResources().getString(R.string.msg_enter_mobile));
            editText.requestFocus();
            return false;
        } else
        if (editText.getText().toString().trim().length() < 10){
            editText.setError(context.getResources().getString(R.string.msg_enter_10digitmobile));
            editText.requestFocus();
            return false;
        }else {

        }

        return true;
    }

    public boolean validatePassword1(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setError(context.getResources().getString(R.string.msg_enter_password));
            editText.requestFocus();
            return false;
        } else
        if (editText.getText().toString().trim().length() < 6){
            editText.setError(context.getResources().getString(R.string.msg_enter_6character_password));
            editText.requestFocus();
            return false;
        }else {

        }

        return true;
    }

    public boolean validatePassword2(EditText editText1, EditText editText2) {
        if (editText2.getText().toString().trim().isEmpty()) {
            editText2.setError(context.getResources().getString(R.string.msg_enter_confirm_password));
            editText2.requestFocus();
            return false;
        } else {

            if (validatePassword1(editText1)){
                return CheckEqualPassword(editText1, editText2);
            }
        }

        return true;
    }

    public boolean CheckEqualPassword(EditText editText1, EditText editText2) {
        String p1 = editText1.getText().toString().trim();
        String p2 = editText2.getText().toString().trim();

        if (!p1.equals(p2)) {
            editText2.setError(context.getResources().getString(R.string.msg_password_not_same));
            editText2.requestFocus();
            return false;
        } else {
        }

        return true;
    }



    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public boolean validateOtp(EditText editText) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.requestFocus();
            editText.setError(context.getResources().getString(R.string.msg_otp));
            return false;
        } else {

        }

        return true;
    }



    public boolean validateIsEmpty(EditText editText, String msg) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.requestFocus();
            editText.setError(msg);
            return false;
        } else {

        }

        return true;
    }



}
