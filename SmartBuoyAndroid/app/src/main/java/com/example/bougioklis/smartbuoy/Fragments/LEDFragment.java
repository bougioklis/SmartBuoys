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

    // h shmadoura pou epilex8ike
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

        // arxikopoihsh tou antikeimenou
        global = ((Global) getActivity().getApplicationContext());
        buoy = global.buoyList.get(global.markerClickIndex);

        // vriskoume ta views
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


        //click listeners gia ta led kai update ston server
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

        //elegxoume sthn bash an to rgb fwtaki einia off h exei kapoio xrwma!!
        // an exei kapoio xrwma to 8etoume san background color sto antistoixo rgb button
        // an einia off tote to kanoume disabled
        //exoume ena switchaki to opoio mas epitrepei na anoiksoume kai na kleisoume ta fwta.
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

        // on clicklisteners gia ta switches twn fwtwn
        rgb1Switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rgb1Switch.isChecked()){
                    // an einai checked shmainei oti prepei na kanoume disabled to fws
                    rgb1.setBackgroundResource(android.R.drawable.btn_default);
                    rgb1Switch.setText(getString(R.string.offLight));
                    rgb1.setEnabled(false);
                    global.buoyList.get(global.markerClickIndex).setRGB1("off");
                }else{
                    //to anoigoume kanoume to koumpi clickable
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
                    // an einai checked shmainei oti prepei na kanoume disabled to fws
                    rgb2.setBackgroundResource(android.R.drawable.btn_default);
                    rgb2.setEnabled(false);
                    rgb2Switch.setText(getString(R.string.offLight));
                    global.buoyList.get(global.markerClickIndex).setRGB2("off");
                }else{
                    //to anoigoume kanoume to koumpi clickable
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
                    // an einai checked shmainei oti prepei na kanoume disabled to fws
                    rgb3.setBackgroundResource(android.R.drawable.btn_default);
                    rgb3.setEnabled(false);
                    rgb3Switch.setText(getString(R.string.offLight));
                    global.buoyList.get(global.markerClickIndex).setRGB3("off");
                }else{
                    //to anoigoume kanoume to koumpi clickable
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
//                Toast.makeText(getActivity().getApplicationContext(), "Selected Color : " + color, Toast.LENGTH_LONG).show();
                // metatropi tou xrwmatos apo int se hex
                String hexColor = String.format("#%06X", (0xFFFFFF & color));
                switch (id){
                    case 1: {
                        // allazoume to antikeimenos sthn lista maskai to background tou koumpiou
                        global.buoyList.get(global.markerClickIndex).setRGB1(hexColor);
                        rgb1.setBackgroundColor(Color.parseColor(global.buoyList.get(global.markerClickIndex).getRGB1()));
                        break;
                    }
                    case 2:{
                        // allazoume to antikeimenos sthn lista maskai to background tou koumpiou
                        global.buoyList.get(global.markerClickIndex).setRGB2(hexColor);
                        rgb2.setBackgroundColor(Color.parseColor(global.buoyList.get(global.markerClickIndex).getRGB2()));
                        break;
                    }
                    case 3:{
                        // allazoume to antikeimenos sthn lista maskai to background tou koumpiou
                        global.buoyList.get(global.markerClickIndex).setRGB3(hexColor);
                        rgb1.setBackgroundColor(Color.parseColor(global.buoyList.get(global.markerClickIndex).getRGB3()));
                        break;
                    }
                }
            }
        });
        dialog.show();
        updateBuoy();
    }

    private void updateBuoy(){

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
                                //IOException
                                Toast.makeText(global.context, R.string.unableToUpdate, Toast.LENGTH_LONG).show();
                                global.flagIOException= false;
                            }
                            if (result[0].equals("-1")){
                                //exception apo ton server
                                Toast.makeText(global.context, R.string.serverError, Toast.LENGTH_LONG).show();
                            }else {
                                //epituxws updated
                                Toast.makeText(global.context, R.string.updateSuccessful, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
