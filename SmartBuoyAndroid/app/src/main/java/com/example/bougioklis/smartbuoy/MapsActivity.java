package com.example.bougioklis.smartbuoy;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.example.bougioklis.smartbuoy.Classes.GPS;
import com.example.bougioklis.smartbuoy.Classes.Global;
import com.example.bougioklis.smartbuoy.Fragments.FragmentDialog;
import com.example.bougioklis.smartbuoy.MenuOptions.AboutUsActivity;
import com.example.bougioklis.smartbuoy.MenuOptions.SettingsAtivity;
import com.example.bougioklis.smartbuoy.MenuOptions.TutorialActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;


// TODO: 01-Nov-17  ana posh wra na katevazei ksana apo ton server? 
public class MapsActivity extends AppCompatActivity {

    // h lista mas 8a exei stis prwtes 8eseis tis simadoures kai sthn teleutaia marker me thn topo8esia tou user
    private List<OverlayItem> items = new ArrayList<>();
    private Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_maps);

        global = ((Global) getApplicationContext());
        global.activity = MapsActivity.this;
        global.context = MapsActivity.this;


        GPS gps = new GPS(getApplicationContext());
//pernoume tis suntetagmenes tou user
        global.latitude = gps.getLatitude();
        global.longitude = gps.getLongitude();


       final MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        //dhmiourgoume markers me tis shmadoures
        for(int i=0;i<global.buoyList.size();i++){
            items.add(new OverlayItem("","",new GeoPoint(global.buoyList.get(i).getLat(),global.buoyList.get(i).getLng())));
            Drawable marker = global.buoyList.get(i).getMarkerIcon();
            items.get(items.size()-1).setMarker(marker);
        }

        IMapController mapController = map.getController();
        mapController.setZoom(13);




        items.add(new OverlayItem("Η Τοποθεσία σας", "", new GeoPoint(global.latitude,global.longitude))); // Lat/Lon decimal degrees
        //the overlay with onClick Listener and On ItemLOnglistener
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<>(
                this, items,  //  <--------- added Context this as first parameter
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override

                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        if(index != items.size()-1) {

                            global.markerClickIndex=index;
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentDialog overlay = new FragmentDialog();
                            overlay.show(fm, "FragmentDialog");
                        }
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        if(index!=items.size()-1){
                            items.get(index).setMarker(global.buoyList.get(index).getSelectedMarker());
                            //----------------------------------------------------------------------------------
//                            Overlay touchOverlay = new Overlay(MapsActivity.this) {
//                                ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;
//
//                                @Override
//                                public void draw(Canvas arg0, MapView arg1, boolean arg2) {
//
//                                }
//
//                                @Override
//                                public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {
//
//                                    final Drawable marker = getApplicationContext().getResources().getDrawable(R.drawable.buoy);
//                                    Projection proj = mapView.getProjection();
//                                    GeoPoint loc = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());
//                                    String longitude = Double.toString(((double) loc.getLongitudeE6()) / 1000000);
//                                    String latitude = Double.toString(((double) loc.getLatitudeE6()) / 1000000);
//                                    System.out.println("- Latitude = " + latitude + ", Longitude = " + longitude);
//                                    return true;
//                                }
//                            };
//                                    ArrayList<OverlayItem> overlayArray = new ArrayList<OverlayItem>();
//                                    OverlayItem mapItem = new OverlayItem("", "", new GeoPoint((((double)loc.getLatitudeE6())/1000000), (((double)loc.getLongitudeE6())/1000000)));
//                                    mapItem.setMarker(marker);
//                                    overlayArray.add(mapItem);
//                                    if(anotherItemizedIconOverlay==null){
//                                        anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getApplicationContext(), overlayArray,null);
//                                        mapView.getOverlays().add(anotherItemizedIconOverlay);
//                                        mapView.invalidate();
//                                    }else{
//                                        mapView.getOverlays().remove(anotherItemizedIconOverlay);
//                                        mapView.invalidate();
//                                        anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getApplicationContext(), overlayArray,null);
//                                        mapView.getOverlays().add(anotherItemizedIconOverlay);
//                                    }
//                                    //      dlgThread();
//                                    return true;
//                                }
//                            };
//                            map.getOverlays().add(touchOverlay);
                            //----------------------------------------------------------------------------------
                        }
                        return false;
                    }
                });  // <----- removed the mResourceProxy parameter
        mOverlay.setFocusItemsOnTap(true);

        map.getOverlays().add(mOverlay);

        GeoPoint startPoint = new GeoPoint(global.latitude, global.longitude);
        mapController.setCenter(startPoint);

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsAtivity.class));
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
    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }
}
