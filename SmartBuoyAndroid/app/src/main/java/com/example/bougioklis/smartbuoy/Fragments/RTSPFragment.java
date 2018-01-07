package com.example.bougioklis.smartbuoy.Fragments;


import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.bougioklis.smartbuoy.Classes.BuoyClass;
import com.example.bougioklis.smartbuoy.Classes.Global;
import com.example.bougioklis.smartbuoy.R;
import com.example.bougioklis.smartbuoy.RTSPActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RTSPFragment extends Fragment{
    // TODO: 05-Nov-17 edw an o xrhsths er8ei se auto to fragment kai kanei swipe back tote sunexizei na paizei to
    // TODO: 05-Nov-17 videaki!! kai feugei apo to focus alla fainetai sthn o8onh!!
    // TODO: 05-Nov-17 an valoume koumpi kai anoigei ena alla activity 8a paizei kanonika!!!
    Global global;
    Button showStream;
//    BuoyClass buoy;
//
//    final static String USERNAME = "admin";
//    final static String PASSWORD = "camera";
//    final static String RTSP_URL = "rtsp://10.0.1.7:554/play1.sdp";
//    final static String RTSP_URL_TEST ="rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov";
//
//    private MediaPlayer mediaPlayer;
//    private SurfaceHolder surfaceHolder;
//    SurfaceView surfaceView;

    public RTSPFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rts, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        global = ((Global) getActivity().getApplicationContext());

        //aplo intent se kainourgio activity
        showStream = (Button) view.findViewById(R.id.showStream);
        showStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.context.startActivity(new Intent(global.activity, RTSPActivity.class));
            }
        });
    }
}
