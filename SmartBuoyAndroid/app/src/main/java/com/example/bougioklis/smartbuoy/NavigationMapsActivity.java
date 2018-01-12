package com.example.bougioklis.smartbuoy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.bougioklis.smartbuoy.Classes.BuoyClass;
import com.example.bougioklis.smartbuoy.Classes.Global;
import com.example.bougioklis.smartbuoy.Fragments.FragmentDialog;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

public class NavigationMapsActivity extends AppCompatActivity {

    private List<OverlayItem> items = new ArrayList<>();
    private Global global;

    MapView map;

    BuoyClass buoy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_navigation_maps);

        global = ((Global) getApplicationContext());

        buoy = global.buoyList.get(global.markerClickIndex);

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);


        items.add(new OverlayItem("", "", new GeoPoint(buoy.getLat(), buoy.getLng())));
        Drawable marker = buoy.getSelectedMarker();
        items.get(items.size() - 1).setMarker(marker);

        IMapController mapController = map.getController();
        mapController.setZoom(13);


        items.add(new OverlayItem("Η Τοποθεσία σας", "", new GeoPoint(global.latitude, global.longitude))); // Lat/Lon decimal degrees

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

        GeoPoint startPoint = new GeoPoint(global.latitude, global.longitude);
        mapController.setCenter(startPoint);

        MapEventsReceiver mReceive = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper( final  GeoPoint p) {
                Toast.makeText(getBaseContext(),p.getLatitude() + " - "+p.getLongitude(),Toast.LENGTH_LONG).show();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NavigationMapsActivity.this);
                alertDialogBuilder.setMessage(getApplicationContext().getString(R.string.alertDialog) + " "+p.getLatitude()+" , "+p.getLongitude());
                        alertDialogBuilder.setPositiveButton("Ναί",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        global.buoyList.get(global.markerClickIndex).setTargetLat(p.getLatitude());
                                        global.buoyList.get(global.markerClickIndex).setTargetLng(p.getLongitude());
                                        updateBuoy();
                                    }
                                });

                alertDialogBuilder.setNegativeButton("Άκυρο",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };


        MapEventsOverlay OverlayEvents = new MapEventsOverlay(getBaseContext(), mReceive);
        map.getOverlays().add(OverlayEvents);
    }


    private void updateBuoy() {

        final String[] result = new String[1];

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //kanoume update apo ton server
                    result[0] = global.updateBuoyNavigation(global.buoyList.get(global.markerClickIndex));
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
                                finish();
                            }
                        }
                    });
                }
            }
        }).start();
    }


}
