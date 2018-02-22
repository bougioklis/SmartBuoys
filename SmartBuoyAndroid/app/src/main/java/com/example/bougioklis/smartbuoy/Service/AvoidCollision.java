package com.example.bougioklis.smartbuoy.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.bougioklis.smartbuoy.Classes.BuoyClass;
import com.example.bougioklis.smartbuoy.Classes.Global;

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
        timer.schedule(timerTask, 30000, 30000);
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
                        }finally {
                            if(!result[0].isEmpty()){

                                String parts[] = result[0].split("###");
                                String finalMessage="" ;

                                for( int i =0 ;i < parts.length;i++){
                                    BuoyClass buoyOne = global.buoyList.get(Integer.parseInt(parts[i].substring(0,parts[i].indexOf(",")))-1);
                                    BuoyClass buoyTwo = global.buoyList.get(Integer.parseInt(parts[i].substring(parts[i].indexOf(",")))-1);

                                    finalMessage +="two Buoys are dangerously close\n" +" Buoy 1 :" + buoyOne.getId() +"  Buoy2 : "+buoyTwo.getId();

                                }

                                System.out.println(finalMessage);

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
}
