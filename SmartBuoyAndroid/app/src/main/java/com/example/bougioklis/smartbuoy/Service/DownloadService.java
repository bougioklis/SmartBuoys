package com.example.bougioklis.smartbuoy.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.bougioklis.smartbuoy.Classes.Global;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Bougioklis on 11-Jan-18.
 *
 * Background service which  every 30 seconds it download the Buoys
 */

public class DownloadService extends Service {

    private Global global;
    private TimerTask timerTask;

    public DownloadService() {
        super();
        Log.i("Service", "Just Started");
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
        return START_REDELIVER_INTENT;
    }

    private void startTimer() {
        //set a new Timer
        Timer timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 30 seconds
        // every 30 secs download from DB
        timer.schedule(timerTask, 30000, 30000);
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // we clear the buoyList
                            global.buoyList.clear();
                            // we redownload the Buoys
                            global.buoyList=global.downloadBuoysFromService();
                        } catch (Exception e) {
                            Log.i("Thread ex", e.toString());
                        }
                    }
                }).start();
            }
        };
    }
}
