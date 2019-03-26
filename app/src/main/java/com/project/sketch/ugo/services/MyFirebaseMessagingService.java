package com.project.sketch.ugo.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.project.sketch.ugo.R;
import com.project.sketch.ugo.screen.Splash;
import com.project.sketch.ugo.utils.Constants;
import com.project.sketch.ugo.utils.SharedPref;

import java.util.List;

/**
 * Created by Developer on 1/23/18.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";
    private SharedPref sharedPref;
    private int type = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "title: " + remoteMessage.getData().get("title"));
        Log.d(TAG, "body: " + remoteMessage.getData().get("body"));

        sharedPref = new SharedPref(getApplicationContext());


     // type 1 = driver accept
     // type 2 = start trip
     // type 3 = end trip
     // type 4 = reach near pickup location
     // type 5 = cancel by driver

        if (sharedPref.idFirstLogin()){

            try {

                NotificationManager notificationManager =
                        (NotificationManager) getApplicationContext()
                                .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();

            }catch (Exception e){
                e.printStackTrace();
            }


            try{

                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                type = Integer.parseInt(remoteMessage.getData().get("type"));


                if (applicationInForeground()) {

                    scheduleNotificationInForeground(title, body, type);

                }else {
                    // on background ....
                    scheduleNotificationInBackground(title, body, type);

                }


                Log.d(TAG, "running activity = "+getForegroundActivity());


            }catch (Exception e){
                e.printStackTrace();
            }


        }


    }

    public void scheduleNotificationInForeground(String title, String body, int type){
        switch (type){

            case 1:
                sendNotification(title, body);
                sharedPref.saveIsDriverAccept(true);
                responseForConfirmBookingPage(getApplicationContext(), body, type);

                break;
            case 2:
                sendNotification(title, body);
                setRideStartOrNot(true);

                sharedPref.saveDriverIsReachingThePickupLocation(true);
                responseForConfirmBookingPage(getApplicationContext(), body, type);


                break;
            case 3:
                sendNotification(title, body);
                setRideComplete(true);
                setRideStartOrNot(false);
                sharedPref.saveBookingId("");
                sharedPref.saveDriverIsReachingThePickupLocation(false);

                responseForConfirmBookingPage(getApplicationContext(), body, type);

                endTripResponce(getApplicationContext(), body, type);

                break;
            case 4:
                sendNotification(title, body);

                responseForConfirmBookingPage(getApplicationContext(), body, type);

                break;

            case 5:
                sendNotification(title, body);

                sharedPref.setFreeForBooking(true);
                sharedPref.saveBookingId("");
                sharedPref.saveDriverIsReachingThePickupLocation(false);
                setRideStartOrNot(false);
                setRideComplete(true);


                cancelTripByDriverResponce(getApplicationContext(), body, type);

                break;

        }

    }


    public void scheduleNotificationInBackground(String title, String body, int type){
        switch (type){

            case 1:
                sendNotification(title, body);
                sharedPref.saveIsDriverAccept(true);
                responseForConfirmBookingPage(getApplicationContext(), body, type);

                break;
            case 2:
                sendNotification(title, body);
                setRideStartOrNot(true);

                sharedPref.saveDriverIsReachingThePickupLocation(true);
                responseForConfirmBookingPage(getApplicationContext(), body, type);


                break;
            case 3:
                sendNotification(title, body);
                setRideComplete(true);
                setRideStartOrNot(false);
                sharedPref.saveBookingId("");
                sharedPref.saveDriverIsReachingThePickupLocation(false);
                sharedPref.setFreeForBooking(true);

                responseForConfirmBookingPage(getApplicationContext(), body, type);

                endTripResponce(getApplicationContext(), body, type);

                break;
            case 4:
                sendNotification(title, body);

                responseForConfirmBookingPage(getApplicationContext(), body, type);

                break;

            case 5:
                sendNotification(title, body);

                sharedPref.setFreeForBooking(true);
                sharedPref.saveBookingId("");
                sharedPref.saveDriverIsReachingThePickupLocation(false);
                setRideStartOrNot(false);
                setRideComplete(true);

                cancelTripByDriverResponce(getApplicationContext(), body, type);


                break;
        }

    }


    private void sendNotification(String messageTitle, String messageBody) {

        Intent intent = new Intent(this, Splash.class);
        //intent.putExtra("id", chatroom_id);
        //intent.setAction(chatroom_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 /* request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        long[] pattern = {400};
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        android.support.v4.app.NotificationCompat.Builder notificationBuilder =  new android.support.v4.app.NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            showNotificationForOreo(getApplicationContext(), messageTitle, messageBody);

        }else
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // notificationManager.notify(new NotificationID().getID(), notificationBuilder.build());
            notificationManager.notify(getNotId(), notificationBuilder.build());
        } else {
            notificationManager.notify(getNotId(), notificationBuilder.build());
        }


        /*if (applicationInForeground()){
            notificationManager.cancel(getNotId());
        }*/



    }


    public void showNotificationForOreo(Context context, String title, String body) {

        Intent intent = new Intent(this, Splash.class);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "UGO";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.logo_ugo)
                .setContentTitle(title)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(getNotId(), mBuilder.build());
    }


    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.logo_ugo : R.mipmap.logo_ugo;
    }

    private int getNotId(){
        if (type > 0){
            return type;
        }else {
            int id = (int) System.currentTimeMillis();
            return id;
        }
    }


    private void setRideStartOrNot(boolean bookingStartOrNot){
        sharedPref.saveRiding(bookingStartOrNot);
    }


    private void setRideComplete(boolean bookingStartOrNot){
        sharedPref.setRideComplete(bookingStartOrNot);
    }



    private boolean applicationInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> services = activityManager.getRunningAppProcesses();
        boolean isActivityFound = false;

        if (services.get(0).processName.equalsIgnoreCase(getPackageName())) {
            isActivityFound = true;
        }

        return isActivityFound;
    }


    private String getForegroundActivity(){
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        //Log.d("TAG", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        componentInfo.getPackageName();

        return taskInfo.get(0).topActivity.getClassName();
    }


    ////////////////////////////////////////////////////////////////////////



    /*static void noticeInTrackYourRide(Context context, String message) {
        Intent intent = new Intent(Constants.key_trackyourride);
        //put whatever data you want to send, if any
        intent.putExtra("message", message);
        //send broadcast
        context.sendBroadcast(intent);
    }*/

    static void responseForConfirmBookingPage(Context context, String message, int type_) {
        Intent intent = new Intent(Constants.key_confirmbooking);
        //put whatever data you want to send, if any
        intent.putExtra("message", message);
        intent.putExtra("type", type_);
        //send broadcast
        context.sendBroadcast(intent);
    }


    static void endTripResponce(Context context, String message, int type_) {
        Intent intent = new Intent(Constants.key_trackyourride);
        //put whatever data you want to send, if any
        intent.putExtra("message", message);
        intent.putExtra("type", type_);
        //send broadcast
        context.sendBroadcast(intent);
    }

    static void cancelTripByDriverResponce(Context context, String message, int type_) {
        Intent intent = new Intent(Constants.key_confirmbooking);
        //put whatever data you want to send, if any
        intent.putExtra("message", message);
        intent.putExtra("type", type_);
        //send broadcast
        context.sendBroadcast(intent);
    }


}



