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

    //XML views
    TextView location, orientation,buoyID;
    Switch hover;


    //which buoy has been selected
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

        // select buoy
        global =((Global) getActivity().getApplicationContext());
        buoy = global.buoyList.get(global.markerClickIndex);

        // XML views
        location = (TextView) view.findViewById(R.id.location);
        orientation  = (TextView) view.findViewById(R.id.orientation);
        buoyID = (TextView) view.findViewById(R.id.buoyID);

        hover = (Switch) view.findViewById(R.id.hoverSwitch);



        location.setText("Η σημαδούρα βρίσκεται στις συντεταγμένες: "+buoy.getLat()+" "+buoy.getLng());
        orientation.setText("Η σημαδούρα κοιτάει: "+buoy.getOrientationString());
        buoyID.setText("Το ID της σημαδούρας είναι " +buoy.getId() );

        hover.setChecked(buoy.isHoverflag());


        hover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //modify the specific buoy on the buoyList and then we update DB
                global.buoyList.get(global.markerClickIndex).setHoverflag(!buoy.isHoverflag());
                updateBuoy();
            }
        });

    }

    private void updateBuoy(){

        final String[] result = new String[1];

        //background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //update on server
                   result[0] = global.updateBuoys(global.buoyList.get(global.markerClickIndex));
                } catch (Exception e) {
                    Log.i("Thread Exce", e.toString());
                } finally {
                    global.activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //if there is an IOException
                            if (global.flagIOException) {
                                Toast.makeText(global.context, R.string.unableToUpdate, Toast.LENGTH_LONG).show();
                                global.flagIOException= false;
                            }
                            if (result[0].equals("-1")){
                                //if there is a server error
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
