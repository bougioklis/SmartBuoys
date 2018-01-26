package com.example.bougioklis.smartbuoy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bougioklis.smartbuoy.Classes.BuoyClass;
import com.example.bougioklis.smartbuoy.Classes.Global;
import com.example.bougioklis.smartbuoy.Fragments.FragmentDialog;
import com.example.bougioklis.smartbuoy.MqttHelper.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class RTSPActivity extends AppCompatActivity {

    Global global;
    BuoyClass buoy;
    Switch cameraSwitch;
    SeekBar throttle,steering;
    TextView throttleTV,steeringTV;
    Button stopButton;

    // h lista mas 8a exei stis prwtes 8eseis tis simadoures kai sthn teleutaia marker me thn topo8esia tou user
    private List<OverlayItem> items = new ArrayList<>();

    final static String USERNAME = "admin";
    final static String PASSWORD = "camera";
    final static String RTSP_URL = "rtsp://10.0.1.7:554/play1.sdp";
    final static String RTSP_URL_TEST = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov";

    MqttHelper mqttHelper;

    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_rtsp);

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);



        //XML Views
        global = ((Global) getApplicationContext());
        throttle = (SeekBar) findViewById(R.id.throttle);
        throttleTV = (TextView) findViewById(R.id.throttleTextView);
        steering = (SeekBar) findViewById(R.id.steering);
        steeringTV = (TextView) findViewById(R.id.steeringTextView);
        stopButton = (Button) findViewById(R.id.stop);

        //onClick stop the buoy
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttHelper.connect(buoy.getDriveTopicID());
                mqttHelper.publishMessage(buoy.getDriveTopicID(),"Throttle:0///Steering:90");
            }
        });

        final int[] progressThrottle = new int[1];
        final int[] progressSterring = new int[1];

        // show the throttle to user
        throttle.setProgress(global.buoyList.get(global.markerClickIndex).getThrottle());
        progressThrottle[0]= global.buoyList.get(global.markerClickIndex).getThrottle();
        throttleTV.setText(getString(R.string.throttle)+" : "+global.buoyList.get(global.markerClickIndex).getThrottle()+"");

        //show the steering position to user
        steering.setProgress(global.buoyList.get(global.markerClickIndex).getSteering());
        progressSterring[0]=global.buoyList.get(global.markerClickIndex).getSteering();
        steeringTV.setText(getString(R.string.steering)+" : "+global.buoyList.get(global.markerClickIndex).getSteering()+"");

        throttle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressThrottle[0] =progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                throttleTV.setText("Tracking Throttle");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //set to throttle textview the progress
                throttleTV.setText(getApplicationContext().getString(R.string.throttle)+": "+ progressThrottle[0]);
                //set on global buoylist the throttle power
                global.buoyList.get(global.markerClickIndex).setThrottle(progressThrottle[0]);
                // connect to mqtt
                mqttHelper.connect(buoy.getDriveTopicID());
                //publish on mqtt
                mqttHelper.publishMessage(buoy.getDriveTopicID(),"Throttle:"+progressThrottle[0]+"///Steering:"+progressSterring[0]);
            }
        });

        steering.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressSterring[0] = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                steeringTV.setText("Tracking Steering");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // round to 0 ,45,90,135,180
                if ((progressSterring[0] >= 0 && progressSterring[0] < 45) && progressSterring[0] < 23 )
                    progressSterring[0] = 0 ;
                else if ((progressSterring[0] > 0 && progressSterring[0] <= 45) && progressSterring[0] >= 23)
                    progressSterring[0] = 45 ;
                else if ((progressSterring[0] > 45 && progressSterring[0] < 90) && progressSterring[0] < 68)
                    progressSterring[0] = 45 ;
                else if ((progressSterring[0] > 45 && progressSterring[0] <= 90) && progressSterring[0] >= 68)
                    progressSterring[0] = 90 ;
                else if ((progressSterring[0] > 90 && progressSterring[0] < 135) && progressSterring[0] < 113)
                    progressSterring[0] = 90;
                else if ((progressSterring[0] > 90 && progressSterring[0] <= 135) && progressSterring[0] >= 113)
                    progressSterring[0] = 135;
                else if ((progressSterring[0] > 135 && progressSterring[0] < 180) && progressSterring[0] < 158)
                    progressSterring[0] = 135;
                else if ((progressSterring[0] > 135 && progressSterring[0] <= 180) && progressSterring[0] >= 158)
                    progressSterring[0] = 180;



                //same as throttle SeekBar
                steering.setProgress(progressSterring[0]);
                steeringTV.setText(getApplicationContext().getString(R.string.steering)+": "+progressSterring[0]);
                global.buoyList.get(global.markerClickIndex).setSteering(progressSterring[0]);
                mqttHelper.connect(buoy.getDriveTopicID());
                mqttHelper.publishMessage(buoy.getDriveTopicID(),"Throttle:"+progressThrottle[0]+"///Steering:"+progressSterring[0]);
            }
        });


        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);


        buoy = global.buoyList.get(global.markerClickIndex);

        startMqtt();

        //add two markers on the map one for the user and one for the buoy
        items.add(new OverlayItem("", "", new GeoPoint(buoy.getLat(), buoy.getLng())));
        Drawable marker = buoy.getMarkerIcon();
        items.get(items.size() - 1).setMarker(marker);


        IMapController mapController = map.getController();
        mapController.setZoom(13);

        items.add(new OverlayItem("Η Τοποθεσία σας", "", new GeoPoint(global.latitude, global.longitude))); // Lat/Lon decimal degrees
        //the overlay with onClick Listener and On ItemLOnglistener
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<>(
                this, items,  //  <--------- added Context this as first parameter
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override

                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {

                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                });
        mOverlay.setFocusItemsOnTap(true);

        map.getOverlays().add(mOverlay);

        GeoPoint buoyPoint = new GeoPoint(buoy.getLat(), buoy.getLng());
        mapController.setCenter(buoyPoint);

        if (buoy.isCameraflag()){
            showRtspStream();
        }

        cameraSwitch = (Switch) findViewById(R.id.cameraSwitch);

        cameraSwitch.setChecked(buoy.isCameraflag());

        cameraSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //modify the list and update DB
                global.buoyList.get(global.markerClickIndex).setCameraflag(!buoy.isCameraflag());
                updateBuoy();
                if (global.buoyList.get(global.markerClickIndex).isCameraflag()) {
                    //we show rtsp stream
                    showRtspStream();
                } else {
                    //we pause the camera
                    mediaPlayer.pause();
//                    mediaPlayer.release();
                }
            }
        });
    }

    public void showRtspStream(){
        //rtsp stream
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDisplay(surfaceHolder);

                Context context = global.activity.getApplicationContext();

                Uri source = Uri.parse(RTSP_URL_TEST);

                try {
                    // Specify the IP camera's URL and auth headers.
                    mediaPlayer.setDataSource(context, source);

                    // Begin the process of setting up a video stream.
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.prepareAsync();
                } catch (Exception e) {
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mediaPlayer.release();

            }
        });
        surfaceHolder.setFixedSize(320, 240);
    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }

    private Map<String, String> getRtspHeaders() {
        Map<String, String> headers = new HashMap<>();
        String basicAuthValue = getBasicAuthValue(USERNAME, PASSWORD);
        headers.put("Authorization", basicAuthValue);
        return headers;
    }

    private String getBasicAuthValue(String usr, String pwd) {
        String credentials = usr + ":" + pwd;
        int flags = Base64.URL_SAFE | Base64.NO_WRAP;
        byte[] bytes = credentials.getBytes();
        return "Basic " + Base64.encodeToString(bytes, flags);
    }

    private void updateBuoy() {

        final String[] result = new String[1];

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //background thread
                    result[0] = global.updateBuoys(global.buoyList.get(global.markerClickIndex));
                } catch (Exception e) {
                    Log.i("Thread Exce", e.toString());
                } finally {
                    global.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (global.flagIOException) {
                                //IOException
                                Toast.makeText(global.context, R.string.unableToUpdate, Toast.LENGTH_LONG).show();
                                global.flagIOException = false;
                            }
                            if (result[0].equals("-1")) {
                                // server side error
                                Toast.makeText(global.context, R.string.serverError, Toast.LENGTH_LONG).show();
                            } else {
                                //updated Successfully
                                Toast.makeText(global.context, R.string.updateSuccessful, Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void startMqtt(){
        mqttHelper = new MqttHelper(getApplicationContext());

        mqttHelper.connect(buoy.getTopicID());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    @Override
    public void onBackPressed(){
        // code here to show dialog
        Log.i("back pressed","pressed");

        // unsubscribe from connected topics
        mqttHelper.unsubscribeFromTopic(buoy.getDriveTopicID());
        mqttHelper.unsubscribeFromTopic(buoy.getTopicID());
        super.onBackPressed();  // optional depending on your needs
    }
}
