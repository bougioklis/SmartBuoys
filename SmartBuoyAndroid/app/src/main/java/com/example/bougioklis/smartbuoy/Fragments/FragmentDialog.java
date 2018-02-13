package com.example.bougioklis.smartbuoy.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.example.bougioklis.smartbuoy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDialog extends DialogFragment {

    public FragmentDialog() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        //dialog box initialization
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setCanceledOnTouchOutside(false);


        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_dialog, container);

        // tab slider
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(sectionsPagerAdapter);

        return view;
    }





    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    /**
     * Used for tab paging...
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // select which fragment will be shown
            switch (position){
                case 0: {
                    return new GeneralFragment();
                }
                case 1: {
                    return new LEDFragment();
                }
                case 2: {
                    return new RTSPFragment();
                }
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //title of each fragment
            switch (position) {
                case 0:
                    return "Γενικές Πληροφορίες";
                case 1:
                    return "Led";
                case 2:
                    return "Live Video";
            }
            return null;
        }
    }

}
