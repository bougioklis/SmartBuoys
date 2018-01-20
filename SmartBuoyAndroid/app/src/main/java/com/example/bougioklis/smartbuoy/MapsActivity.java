package com.example.bougioklis.smartbuoy;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.bougioklis.smartbuoy.Classes.GPS;
import com.example.bougioklis.smartbuoy.Classes.Global;
import com.example.bougioklis.smartbuoy.Fragments.FragmentDialog;
import com.example.bougioklis.smartbuoy.MenuOptions.AboutUsActivity;
import com.example.bougioklis.smartbuoy.MenuOptions.SettingsActivity;
import com.example.bougioklis.smartbuoy.MenuOptions.TutorialActivity;
import com.example.bougioklis.smartbuoy.Service.DownloadService;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


// TODO: 01-Nov-17  ana posh wra na katevazei ksana apo ton server? 
public class MapsActivity extends AppCompatActivity {

    // list with markers on the last position it has the user coor
    private List<OverlayItem> items = new ArrayList<>();
    private Global global;

    private Timer timer;
    private TimerTask timerTask;

    MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_maps);

        global = ((Global) getApplicationContext());
        global.activity = MapsActivity.this;
        global.context = MapsActivity.this;

        Intent serviceIntent = new Intent(this, DownloadService.class);
        startService(serviceIntent);

        GPS gps = new GPS(getApplicationContext());

        //user coor
        global.latitude = gps.getLatitude();
        global.longitude = gps.getLongitude();


        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        try {
            //list with markers
            for (int i = 0; i < global.buoyList.size(); i++) {
                items.add(new OverlayItem("", "", new GeoPoint(global.buoyList.get(i).getLat(), global.buoyList.get(i).getLng())));
                Drawable marker = global.buoyList.get(i).getMarkerIcon();
                items.get(items.size() - 1).setMarker(marker);
            }
        }catch (NullPointerException e){
            // if we have a NullPointerException that means we have a wrong ip so we
            // could not download the buoys. Go to SettingActivity and put the unique id so after the user has finished go again to splashActivity
            startActivity(new Intent(MapsActivity.this,SettingsActivity.class).putExtra("id","SplashActivity"));
            finish();
        }

        IMapController mapController = map.getController();
        mapController.setZoom(13);


        items.add(new OverlayItem("Η Τοποθεσία σας", "", new GeoPoint(global.latitude, global.longitude))); // Lat/Lon decimal degrees
        //the overlay with onClick Listener and On ItemLOnglistener
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<>(
                this, items,  //  <--------- added Context this as first parameter
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override

                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        if (index != items.size() - 1) {

                            global.markerClickIndex = index;
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentDialog overlay = new FragmentDialog();
                            overlay.show(fm, "FragmentDialog");
                        }
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        if (index != items.size() - 1) {
//                            items.get(index).setMarker(global.buoyList.get(index).getSelectedMarker());

                            global.markerClickIndex = index;
                            startActivity(new Intent(MapsActivity.this,NavigationMapsActivity.class));
                        }
                        return false;
                    }



                });  // <----- removed the mResourceProxy parameter
        mOverlay.setFocusItemsOnTap(true);

        map.getOverlays().add(mOverlay);

        GeoPoint startPoint = new GeoPoint(global.latitude, global.longitude);
        mapController.setCenter(startPoint);

        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 15 seconds
        // to update the list with the buoys
        timer.schedule(timerTask, 15000, 15000);

    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(global.hasBuoyBeenUpdated) {

                            // if Buoys list has been updated then  change the flag and
                            global.hasBuoyBeenUpdated=false;

                            //clear the list
                            items.clear();


                            for (int i = 0; i < global.buoyList.size(); i++) {
                                items.add(new OverlayItem("", "", new GeoPoint(global.buoyList.get(i).getLat(), global.buoyList.get(i).getLng())));
                                Drawable marker = global.buoyList.get(i).getMarkerIcon();
                                items.get(items.size() - 1).setMarker(marker);
                            }

                            items.add(new OverlayItem("Η Τοποθεσία σας", "", new GeoPoint(global.latitude, global.longitude))); // Lat/Lon decimal degrees


                            ItemizedIconOverlay<OverlayItem> mOverlay = new ItemizedIconOverlay<>(getApplicationContext(), items,
                                    new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                                        @Override
                                        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                                            if (index != items.size() - 1) {

                                                global.markerClickIndex = index;
                                                FragmentManager fm = getSupportFragmentManager();
                                                FragmentDialog overlay = new FragmentDialog();
                                                overlay.show(fm, "FragmentDialog");
                                            }
                                            return true;
                                        }

                                        @Override
                                        public boolean onItemLongPress(final int index, final OverlayItem item) {

                                            global.markerClickIndex = index;
                                            startActivity(new Intent(MapsActivity.this,NavigationMapsActivity.class));

                                            return false;
                                        }
                                    });
                            map.getOverlays().clear();
                            map.getOverlays().add(mOverlay);
                            map.invalidate();
                        }
                    }

                });

            }
        };
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    // put option menu
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.tutorial:
                startActivity(new Intent(this, TutorialActivity.class));
                return true;
            case R.id.about:
                startActivity(new Intent(this, AboutUsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }
}
