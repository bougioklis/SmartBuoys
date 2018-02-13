package com.example.bougioklis.smartbuoy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.bougioklis.smartbuoy.Classes.BuoyClass;
import com.example.bougioklis.smartbuoy.Classes.Global;

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
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.List;

public class NavigationMapsActivity extends AppCompatActivity {

    private List<OverlayItem> items = new ArrayList<>();
    private Global global;

    private BuoyClass buoy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_navigation_maps);

        global = ((Global) getApplicationContext());

        buoy = global.buoyList.get(global.markerClickIndex);

        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);


        items.add(new OverlayItem("", "", new GeoPoint(buoy.getLat(), buoy.getLng())));
        Drawable marker = buoy.getSelectedMarker();
        items.get(items.size() - 1).setMarker(marker);

        for (int i =0 ;i<global.buoyList.size();i++){
            if (i != global.markerClickIndex) {

                items.add(new OverlayItem("", "", new GeoPoint(global.buoyList.get(i).getLat(), global.buoyList.get(i).getLng())));
                 marker = global.buoyList.get(i).getMarkerIcon();
                items.get(items.size() - 1).setMarker(marker);
            }
        }

        Polygon mPolygon = new Polygon(this);
        mPolygon.setFillColor(Color.argb(75, 13,4,100));

        final double radius = 2000;
        ArrayList<GeoPoint> circlePoints = new ArrayList<>();
        for (float f = 0; f < 360; f += 1){
            circlePoints.add(new GeoPoint(global.latitude , global.longitude ).destinationPoint(radius, f));
        }
        mPolygon.setPoints(circlePoints);
        map.getOverlays().add(mPolygon);

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

                if(distance(buoy.getLat(),p.getLatitude(),buoy.getLng(),p.getLongitude()) > 2000){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NavigationMapsActivity.this);
                    alertDialogBuilder.setMessage("Η Συγκεκριμένες συντεταγμένες είναι εκτός εύρους");
                    alertDialogBuilder.setPositiveButton("Άκυρο!!!",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    arg0.dismiss();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NavigationMapsActivity.this);
                    alertDialogBuilder.setMessage(getApplicationContext().getString(R.string.alertDialog) + " " + p.getLatitude() + " , " + p.getLongitude() +
                            "\n Η σημαδούρα θα χρειαστεί να ταξιδέψει : " + (int) distance(buoy.getLat(), p.getLatitude(), buoy.getLng(), p.getLongitude()) + " μέτρα.");
                    alertDialogBuilder.setPositiveButton("Ναί",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    global.buoyList.get(global.markerClickIndex).setTargetLat(p.getLatitude());
                                    global.buoyList.get(global.markerClickIndex).setTargetLng(p.getLongitude());
                                    updateBuoy();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("Άκυρο", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
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

    private static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters


        distance = Math.pow(distance, 2) ;

        return Math.sqrt(distance);
    }

}
