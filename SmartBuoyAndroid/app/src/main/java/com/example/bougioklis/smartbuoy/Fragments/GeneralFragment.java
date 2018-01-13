package com.example.bougioklis.smartbuoy.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bougioklis.smartbuoy.Classes.BuoyClass;
import com.example.bougioklis.smartbuoy.Classes.Global;
import com.example.bougioklis.smartbuoy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneralFragment extends Fragment{

    //ta stoixeia apo to XML
    TextView location, orientation,buoyID;
    Switch hover;


    // h shmadoura pou pathsame
    BuoyClass buoy;

    Global global;
    public GeneralFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_general, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // briskoume pia shmadoura pathse
        global =((Global) getActivity().getApplicationContext());
        buoy = global.buoyList.get(global.markerClickIndex);

        //briskoume ta views apo to XML
        location = (TextView) view.findViewById(R.id.location);
        orientation  = (TextView) view.findViewById(R.id.orientation);
        buoyID = (TextView) view.findViewById(R.id.buoyID);

        hover = (Switch) view.findViewById(R.id.hoverSwitch);



        location.setText("Η σημαδούρα βρίσκεται στις συντεταγμένες: "+buoy.getLat()+" "+buoy.getLng());
        orientation.setText("Η σημαδούρα κοιτάει: "+buoy.getOrientationString());
        buoyID.setText("Το ID της σημαδούρας είναι " +buoy.getId() );
//        if(buoy.isHoverflag())
//            hover.setChecked(true);
//        else
//            hover.setChecked(false);
        hover.setChecked(buoy.isHoverflag());


        hover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tropopoioume ta stoixea kai kanoume update tis shmadoures ston server
                global.buoyList.get(global.markerClickIndex).setHoverflag(!buoy.isHoverflag());
                updateBuoy();
            }
        });

    }

    private void updateBuoy(){

        final String[] result = new String[1];

        //dhmiourgia background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //kanoume update ston server
                   result[0] = global.updateBuoys(global.buoyList.get(global.markerClickIndex));
                } catch (Exception e) {
                    Log.i("Thread Exce", e.toString());
                } finally {
                    global.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // afou ektelestei to download pigainoume edw
                            //an uparxei IOEXCEPTION
                            if (global.flagIOException) {
                                Toast.makeText(global.context, R.string.unableToUpdate, Toast.LENGTH_LONG).show();
                                global.flagIOException= false;
                            }
                            if (result[0].equals("-1")){
                                //an epestrepse error apo ton server
                                Toast.makeText(global.context, R.string.serverError, Toast.LENGTH_LONG).show();
                            }else {
                                //to update egine epituxws
                                Toast.makeText(global.context, R.string.updateSuccessful, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }
}
