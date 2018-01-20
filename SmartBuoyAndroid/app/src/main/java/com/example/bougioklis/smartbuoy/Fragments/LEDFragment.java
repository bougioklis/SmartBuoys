package com.example.bougioklis.smartbuoy.Fragments;



import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.example.bougioklis.smartbuoy.Classes.BuoyClass;
import com.example.bougioklis.smartbuoy.Classes.Global;
import com.example.bougioklis.smartbuoy.R;

import yuku.ambilwarna.AmbilWarnaDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class LEDFragment extends Fragment {

    Global global;
    //xml views
    Switch led1, led2, led3,rgb1Switch,rgb2Switch,rgb3Switch;
    Button rgb1, rgb2, rgb3;

    // which buoy was selected
    BuoyClass buoy;

    public LEDFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_led, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // global object initialization
        global = ((Global) getActivity().getApplicationContext());
        buoy = global.buoyList.get(global.markerClickIndex);

        //views
        led1 = (Switch) view.findViewById(R.id.led1);
        led2 = (Switch) view.findViewById(R.id.led2);
        led3 = (Switch) view.findViewById(R.id.led3);

        rgb1 = (Button) view.findViewById(R.id.rgb1);
        rgb2 = (Button) view.findViewById(R.id.rgb2);
        rgb3 = (Button) view.findViewById(R.id.rgb3);


        rgb1Switch = (Switch) view.findViewById(R.id.rgb1Switch);
        rgb2Switch = (Switch) view.findViewById(R.id.rgb2Switch);
        rgb3Switch = (Switch) view.findViewById(R.id.rgb3Switch);

        led1.setChecked(buoy.isLed1());
        led2.setChecked(buoy.isLed2());
        led3.setChecked(buoy.isLed3());


        //click listeners for the leds switched and update the DB
        led1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.buoyList.get(global.markerClickIndex).setLed1(!buoy.isLed1());
                updateBuoy();
            }
        });
        led2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.buoyList.get(global.markerClickIndex).setLed2(!buoy.isLed2());
                updateBuoy();
            }
        });
        led3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.buoyList.get(global.markerClickIndex).setLed3(!buoy.isLed3());
                updateBuoy();
            }
        });

        //check if  rgb is off or it has a value!!
        //if if has a value we put that color on button background
        //if rgb is off then we disable the button
        //switch that allows to open or close the rgb
        if (!buoy.getRGB1().equals("off")) {
            rgb1.setBackgroundColor(Color.parseColor(buoy.getRGB1()));
            rgb1Switch.setText(getString(R.string.onLight));
        }else{
            rgb1.setEnabled(false);
            rgb1Switch.setChecked(true);
            rgb1Switch.setText(getString(R.string.offLight));
        }
        if (!buoy.getRGB2().equals("off")) {
            rgb2.setBackgroundColor(Color.parseColor(buoy.getRGB2()));
            rgb2Switch.setText(getString(R.string.onLight));
        }else{
            rgb2.setEnabled(false);
            rgb2Switch.setChecked(true);
            rgb2Switch.setText(getString(R.string.offLight));
        }
        if (!buoy.getRGB3().equals("off")) {
            rgb3.setBackgroundColor(Color.parseColor(buoy.getRGB3()));
            rgb3Switch.setText(getString(R.string.onLight));
        }else{
            rgb3.setEnabled(false);
            rgb3Switch.setChecked(true);
            rgb3Switch.setText(getString(R.string.offLight));
        }

        // on clicklisteners for rgb switched
        rgb1Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rgb1Switch.isChecked()){
                    //if isChecked then we have to disable the light
                    rgb1.setBackgroundResource(android.R.drawable.btn_default);
                    rgb1Switch.setText(getString(R.string.offLight));
                    rgb1.setEnabled(false);
                    global.buoyList.get(global.markerClickIndex).setRGB1("off");
                }else{
                    //else we enable the light and put white background
                    rgb1.setEnabled(true);
                    rgb1Switch.setText(getString(R.string.onLight));
                    global.buoyList.get(global.markerClickIndex).setRGB1("#ffffff");
                }
                updateBuoy();
            }
        });

        rgb2Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rgb2Switch.isChecked()){
                    //if isChecked then we have to disable the light
                    rgb2.setBackgroundResource(android.R.drawable.btn_default);
                    rgb2.setEnabled(false);
                    rgb2Switch.setText(getString(R.string.offLight));
                    global.buoyList.get(global.markerClickIndex).setRGB2("off");
                }else{
                    //else we enable the light and put white background
                    rgb2.setEnabled(true);
                    rgb2Switch.setText(getString(R.string.onLight));
                    global.buoyList.get(global.markerClickIndex).setRGB2("#ffffff");
                }
                updateBuoy();
            }
        });

        rgb3Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rgb3Switch.isChecked()){
                    //if isChecked then we have to disable the light
                    rgb3.setBackgroundResource(android.R.drawable.btn_default);
                    rgb3.setEnabled(false);
                    rgb3Switch.setText(getString(R.string.offLight));
                    global.buoyList.get(global.markerClickIndex).setRGB3("off");
                }else{
                    //else we enable the light and put white background
                    rgb3.setEnabled(true);
                    rgb3Switch.setText(getString(R.string.onLight));
                    global.buoyList.get(global.markerClickIndex).setRGB3("#ffffff");
                }
                updateBuoy();
            }
        });

        rgb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showColorDialog(1);

            }
        });

        rgb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorDialog(2);
            }
        });

        rgb3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorDialog(3);
            }
        });


    }
    public  void showColorDialog(final int id){
        final AmbilWarnaDialog dialog = new AmbilWarnaDialog(global.activity, 0xff0000ff, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            // Executes, when user click Cancel button
            @Override
            public void onCancel(AmbilWarnaDialog dialog){
            }

            // Executes, when user click OK button
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // transform color from int to hex
                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                switch (id){
                    case 1: {
                        // update buoy object and set button's background
                        global.buoyList.get(global.markerClickIndex).setRGB1(hexColor);
                        rgb1.setBackgroundColor(Color.parseColor(global.buoyList.get(global.markerClickIndex).getRGB1()));
                        break;
                    }
                    case 2:{
                        // update buoy object and set button's background
                        global.buoyList.get(global.markerClickIndex).setRGB2(hexColor);
                        rgb2.setBackgroundColor(Color.parseColor(global.buoyList.get(global.markerClickIndex).getRGB2()));
                        break;
                    }
                    case 3:{
                        // update buoy object and set button's background
                        global.buoyList.get(global.markerClickIndex).setRGB3(hexColor);
                        rgb3.setBackgroundColor(Color.parseColor(global.buoyList.get(global.markerClickIndex).getRGB3()));
                        break;
                    }
                }
            }
        });
        dialog.show();
        //update on server
        updateBuoy();
    }

    private void updateBuoy(){

        final String[] result = new String[1];

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   //update on DB
                    result[0] = global.updateBuoys(global.buoyList.get(global.markerClickIndex));
                } catch (Exception e) {
                    Log.i("Thread Exce", e.toString());
                } finally {
                    global.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // afou ektelestei to download pigainoume edw
                            if (global.flagIOException) {
                                //IOException
                                Toast.makeText(global.context, R.string.unableToUpdate, Toast.LENGTH_LONG).show();
                                global.flagIOException= false;
                            }
                            if (result[0].equals("-1")){
                                //server error
                                Toast.makeText(global.context, R.string.serverError, Toast.LENGTH_LONG).show();
                            }else {
                                //updated successfully
                                Toast.makeText(global.context, R.string.updateSuccessful, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
