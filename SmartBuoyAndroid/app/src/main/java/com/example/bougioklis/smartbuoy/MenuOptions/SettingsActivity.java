package com.example.bougioklis.smartbuoy.MenuOptions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bougioklis.smartbuoy.Classes.Global;
import com.example.bougioklis.smartbuoy.R;
import com.example.bougioklis.smartbuoy.SplashActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText ip,cameraIp;

    private Global global;

    private String intentHelper = null;

    public static final String sharedPreferenceID = "IP_Camera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_activity);

        global = ((Global) getApplicationContext());

        Intent intent =this.getIntent();

        // if intent has extra id  then put that string ti intentHelper
        if (intent.hasExtra("id")) {
            intentHelper = intent.getExtras().getString("id");
        }

        Button submit = (Button) findViewById(R.id.submit);
        ip = (EditText) findViewById(R.id.ip);
        cameraIp = (EditText) findViewById(R.id.rtsp);

        // initialize the shared preference
        SharedPreferences prefs = getSharedPreferences(sharedPreferenceID, MODE_PRIVATE);
        String server_ip = prefs.getString("IP", null);
        String camera_ip = prefs.getString("CameraIP",null);

        //if these two vars are null then the user has to enter the ip
        if (server_ip != null ) {
            ip.setText(server_ip);
            Log.i("ip",server_ip);
        }
        if(camera_ip != null){
            cameraIp.setText(camera_ip);
            Log.i("ip",camera_ip);
        }


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ip.getText().toString().isEmpty() || cameraIp.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Παρακαλώ συμπληρώστε και τα δύο πεδία!",Toast.LENGTH_LONG).show();
                }else{
                    //update shared preferences
                    SharedPreferences.Editor editor = getSharedPreferences(sharedPreferenceID, MODE_PRIVATE).edit();
                    editor.putString("IP", ip.getText().toString());
                    editor.putString("CameraIP", cameraIp.getText().toString());
                    editor.apply();

                    String server= ip.getText().toString();
                    //arxikopoihsh twn url
                    global.selectAllURL = "http://" + server + "/WebServer/SelectAllBuoys.php";
                    global.updateURL = "http://" + server + "/WebServer/UpdateBuoys.php";
                    global.MQTTURL = "tcp://" + server + ":1883";

                    Log.i("ip",ip.getText().toString());
                    Log.i("cameraip",cameraIp.getText().toString());
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        // code here to show dialog
        Log.i("back pressed","pressed");

       // if intentHelper is set then go to SplashActivity
       if (intentHelper != null){
           startActivity(new Intent(SettingsActivity.this, SplashActivity.class));
            finish();
       }
        super.onBackPressed();  // optional depending on your needs
    }

}
