package com.example.bougioklis.smartbuoy.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.bougioklis.smartbuoy.Classes.Global;
import com.example.bougioklis.smartbuoy.R;
import com.example.bougioklis.smartbuoy.RTSPActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class RTSPFragment extends Fragment{

    private Global global;

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

        //intent to rtspAct
        Button showStream = (Button) view.findViewById(R.id.showStream);
        showStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.context.startActivity(new Intent(global.activity, RTSPActivity.class));
            }
        });
    }
}
