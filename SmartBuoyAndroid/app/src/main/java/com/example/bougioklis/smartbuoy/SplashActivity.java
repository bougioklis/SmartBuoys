package com.example.bougioklis.smartbuoy;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bougioklis.smartbuoy.Classes.GPS;
import com.example.bougioklis.smartbuoy.Classes.Global;
import com.example.bougioklis.smartbuoy.MenuOptions.SettingsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private GPS gps;
    private Global global;
    String server_ip = null;

    public static String sharedPreferenceID = "IP_Camera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);

        /*
        * initialize everything from the global class
        * check if GPS is available,
        */

        // TODO: 20-Jan-18  check if is connected on wifi and if the specific wifi has our php files

        //initialize global lists
        global = ((Global) getApplicationContext());
        global.buoyList = new ArrayList<>();

        //shared preferences for the ip
        SharedPreferences prefs = getSharedPreferences(sharedPreferenceID, MODE_PRIVATE);
        server_ip = prefs.getString("IP", null);

        Log.i("serverIP IF",(server_ip==null) +"");

        if (server_ip!= null) {
        //if ip is saved on shared preference initialize the url vars
            global.selectAllURL = "http://" + server_ip + "/WebServer/SelectAllBuoys.php";
            global.updateURL = "http://" + server_ip + "/WebServer/UpdateBuoys.php";
            global.navigationUrl = "http://"+server_ip+"/WebServer/NavigationBuoy.php";
            global.MQTTURL = "tcp://" + server_ip + ":1883";
        } else {
            // intent on Settings Activity to change the ip
            Intent intent = new Intent(SplashActivity.this,SettingsActivity.class);
            //unique id to know from which activity we went on settings activity
            intent.putExtra("id","SplashActivity");
            startActivity(intent);
        }


        gps = new GPS(getApplicationContext());
        if (!gps.isGPSEnabled) {
            buildAlertMessageNoGPS();
        }

        if(server_ip!=null){
            //if we have saved ip on shared preferences  download from DB
            downloadFromServer();
        }

    }

    void downloadFromServer(){
        //background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    global.buoyList = global.downloadBuoys();
                } catch (Exception e) {
                    Log.i("Thread Exce", e.toString());
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //IOException
                            if (global.flagIOException) {
                                Toast.makeText(getApplicationContext(), R.string.unableToDownload, Toast.LENGTH_LONG).show();
                                global.flagIOException = false;
                            }
                            continueApplication();
                        }
                    });
                }
            }
        }).start();
    }

    void continueApplication() {

        //an h suskeuh einai neoteri apo marhmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAndRequestPermissions()) {
                startActivity(new Intent(SplashActivity.this, MapsActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(SplashActivity.this, MapsActivity.class));
            finish();
        }
        //zhtw permission gia android>5.0
//        ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        ActivityCompat.requestPermissions(SplashActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
    }


    //zhtame permission gia ta diafora feature tis efarmoghs
    private boolean checkAndRequestPermissions() {
        int permissionFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionExternal = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionFine != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionExternal != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i("onRequestPermission", "Permission callback called-------");
        switch (requestCode) {
            case 1: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.i("Permission", "write & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        startActivity(new Intent(SplashActivity.this, MapsActivity.class));
                        finish();
                    } else {
                        Log.d("Permission", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("Η εφαρμογή χρειάζεται να έχει πρόσβαση στην τοποθεσία σας και να μπορεί να αποθηκεύση στην συσκευή σας δεδομένα",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, R.string.enablePermissions, Toast.LENGTH_LONG)
                                    .show();
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));


                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    // den exoume GPS kai termatizoume thn efarmogh h pame me intent sthn epilogh gia energopoihsh tou gps
    void buildAlertMessageNoGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Το GPS της συσκευής σας είναι απενεργοποιημένο. \rΠαρακαλώ ενεργοποιήστε το.")
                .setCancelable(false)
                .setPositiveButton("Ενεργοποίηση!", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        // intent gia energopoihsh tou gps
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Άκυρο!", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //termatizoume thn efarmogh
                        Toast.makeText(getApplicationContext(), R.string.enableLocationPermission, Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
