package com.example.bougioklis.smartbuoy.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.bougioklis.smartbuoy.Classes.BuoyClass;
import com.example.bougioklis.smartbuoy.Classes.Global;
import com.example.bougioklis.smartbuoy.R;
import com.example.bougioklis.smartbuoy.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Bougioklis on 22-Feb-18.
 */

public class AvoidCollision extends Service {

    Global global;
    private TimerTask timerTask;
    private int counter =0;

    public AvoidCollision() {
        super();
        Log.i("AvoidCollision Service", "Just Started");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        global = ((Global) getApplicationContext());

        startTimer();
        return START_STICKY;
    }

    private void startTimer() {
        //set a new Timer
        Timer timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 30 seconds
        // every 30 secs checks
        timer.schedule(timerTask, 16000, 16000);
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                final String[] result = {null};
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            result[0] = checkCollision();
                        } catch (Exception e) {
                            Log.i("Thread ex", e.toString());
                        } finally {
                            if (!result[0].equals("-1")) {

                                String parts[] = result[0].split("###");
                                String finalMessage = "";

                                for (int i = 0; i < parts.length; i++) {
                                    BuoyClass buoyOne = global.buoyList.get(Integer.parseInt(parts[i].substring(0, parts[i].indexOf(","))) - 1);
                                    BuoyClass buoyTwo = global.buoyList.get(Integer.parseInt(parts[i].substring(parts[i].indexOf(",") + 1)) - 1);

                                    finalMessage += "Buoy1: " + buoyOne.getId() + " , " + " Buoy2 : " + buoyTwo.getId() + "\n";


                                }

                                System.out.println(finalMessage);


//                                Notification.Builder builder = new Notification.Builder(
//                                        getApplicationContext());
//
//                                Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
//                                intent.setAction("notification");
//                                PendingIntent pendingIntent = PendingIntent.getActivity(
//                                        getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);


                                //---------------------------------------


                                if (counter == 0) {
                                    notificationOperations(finalMessage);
                                    counter++;
                                }else{
                                    notificationOperations(finalMessage);
                                }



                                //---------------------------------------

                            }
                        }
                    }
                }).start();
            }
        };
    }

    private String checkCollision() {

        try {
            //http request
            URL url = new URL(global.avoidCollisionUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);

            InputStream inputstream = httpURLConnection.getInputStream();

            //response to stringBuilder
            StringBuilder result = global.inputToString(inputstream);
            Log.i("response", result.toString());
            if (result.toString().equals("-1")) {
                return null;
            } else {
                // response is on buoy1,buoy2###buoy1,buoy2###buoy1,buoy2 format
                return result.toString();
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void notificationOperations(String message){

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(
                getApplicationContext());

        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        intent.setAction("notification");
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Alert")
                .setContentText("Οι παρακάτω σημαδούρες είναι επικίνδυνα κοντά!!" + message)
                .setTicker("Alert")
                .setLights(Color.RED, 3000, 3000)
                // setLights (int argb, int onMs, int offMs)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setAutoCancel(true);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);

        Notification notification = builder.getNotification();
        notificationManager.notify(001, notification);
    }
}
