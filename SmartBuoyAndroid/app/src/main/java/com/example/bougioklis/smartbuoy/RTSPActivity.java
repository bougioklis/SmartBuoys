package com.example.bougioklis.smartbuoy;

import android.content.Context;
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
//        Log.i("topic",buoy.getTopicID());

//        mqttHelper = new MqttHelper(getApplicationContext(),buoy.getTopicID());

//        mqttHelper.connect();
//        mqttHelper.publishMessage("hello world from android");
        global = ((Global) getApplicationContext());
        throttle = (SeekBar) findViewById(R.id.throttle);
        throttleTV = (TextView) findViewById(R.id.throttleTextView);
        steering = (SeekBar) findViewById(R.id.steering);
        steeringTV = (TextView) findViewById(R.id.steeringTextView);
        final int[] progressThrottle = new int[1];
        final int[] progressSterring = new int[1];

        throttle.setProgress(global.buoyList.get(global.markerClickIndex).getThrottle());
        progressThrottle[0]= global.buoyList.get(global.markerClickIndex).getThrottle();
        throttleTV.setText(getString(R.string.throttle)+" : "+global.buoyList.get(global.markerClickIndex).getThrottle()+"");


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
                throttleTV.setText(getApplicationContext().getString(R.string.throttle)+": "+ progressThrottle[0]);
                global.buoyList.get(global.markerClickIndex).setThrottle(progressThrottle[0]);
                mqttHelper.connect(buoy.getDriveTopicID());
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
                steeringTV.setText(getApplicationContext().getString(R.string.steering)+": "+progressSterring[0]);
                global.buoyList.get(global.markerClickIndex).setSteering(progressSterring[0]);
                mqttHelper.connect(buoy.getDriveTopicID());
                mqttHelper.publishMessage(buoy.getDriveTopicID(),"Throttle:"+progressThrottle[0]+"///Steering:"+progressSterring[0]);
            }
        });


        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);


        buoy = global.buoyList.get(global.markerClickIndex);

        startMqtt();

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
                });  // <----- removed the mResourceProxy parameter
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
                //tropopoioume ta stoixeia sthn lista me tis allages
                global.buoyList.get(global.markerClickIndex).setCameraflag(!buoy.isCameraflag());
                updateBuoy();
                if (global.buoyList.get(global.markerClickIndex).isCameraflag()) {
                    showRtspStream();
                } else {
                    //den deixnoume rtsp stream
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
            }
        });
        // Configure the view that renders live video.

//        https://github.com/controlwear/virtual-joystick-android
//        apo to parapanw link einia to joystick

//        JoystickView joystick = (JoystickView) findViewById(R.id.joystick);
//        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
//            @Override
//            public void onMove(int angle, int strength) {
//                System.out.println("angle is " + angle + " strength is " + strength);
//                //sto angleTextview mhpws na kanoume mia sunarthsh na pernaei orientation anti gia moires?
//                angleTextView.setText("Angle is " + angle);
//                strengthTextView.setText("Strength is " + strength + "%");
//                mqttHelper.connect(buoy.getDriveTopicID());
//                mqttHelper.publishMessage(buoy.getDriveTopicID(),"strength:"+strength+"///angle:"+angle);
//
//            }
//        });


    }

    public void showRtspStream(){
        //deixnoume rtsp stream
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDisplay(surfaceHolder);

                Context context = global.activity.getApplicationContext();
//                Map<String, String> headers = getRtspHeaders();
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
                    //kanoume update apo ton server
                    result[0] = global.updateBuoys(global.buoyList.get(global.markerClickIndex));
                } catch (Exception e) {
                    Log.i("Thread Exce", e.toString());
                } finally {
                    global.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // afou ektelestei to download pigainoume edw
                            if (global.flagIOException) {
                                Toast.makeText(global.context, R.string.unableToUpdate, Toast.LENGTH_LONG).show();
                                global.flagIOException = false;
                            }
                            if (result[0].equals("-1")) {
                                Toast.makeText(global.context, R.string.serverError, Toast.LENGTH_LONG).show();
                            } else {
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
//                dataReceived.setText(mqttMessage.toString());

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
        mqttHelper.unsubscribeFromTopic(buoy.getDriveTopicID());
        mqttHelper.unsubscribeFromTopic(buoy.getTopicID());
        super.onBackPressed();  // optional depending on your needs
    }
}
