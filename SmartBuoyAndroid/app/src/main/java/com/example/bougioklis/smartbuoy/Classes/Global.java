package com.example.bougioklis.smartbuoy.Classes;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.bougioklis.smartbuoy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bougioklis on 28-Oct-17.
 */

public class Global extends Application {

    //metablites gia thn ip
    public String selectAllURL, updateURL, MQTTURL;

    // h topo8esia tou user
    public double latitude, longitude;

    // lista me tis simadoures
    public List<BuoyClass> buoyList;

    // o marker pou pataei o index apo to items tou marker antistoixei sthn idia shmadoura me auth apo to buoyList
    public int markerClickIndex;

    //boh8itikes  metavlites gia mesa sta fragment
    public Activity activity;
    public Context context;

    //an ginei kapoio IOException
    public boolean flagIOException = false;

    //sunarthsh gia update twn shmadourwn
    public String updateBuoys(BuoyClass buoy) {

        String updateB = updateURL;

        try {
            URL url = new URL(updateB);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

            //voh8itikes metablites anti gia boolean
            String LED1Helper, LED2Helper, LED3Helper, hoverHelper, cameraHelper;

            if (buoy.isLed1())
                LED1Helper = "1";
            else
                LED1Helper = "0";

            if (buoy.isLed2())
                LED2Helper = "1";
            else
                LED2Helper = "0";

            if (buoy.isLed3())
                LED3Helper = "1";
            else
                LED3Helper = "0";

            if (buoy.isHoverflag())
                hoverHelper = "1";
            else
                hoverHelper = "0";

            if (buoy.isCameraflag())
                cameraHelper = "1";
            else
                cameraHelper = "0";

            String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(buoy.getId() + "", "UTF-8")
                    + "&" + URLEncoder.encode("LED1", "UTF-8") + "=" + URLEncoder.encode(LED1Helper, "UTF-8")
                    + "&" + URLEncoder.encode("LED2", "UTF-8") + "=" + URLEncoder.encode(LED2Helper, "UTF-8")
                    + "&" + URLEncoder.encode("LED3", "UTF-8") + "=" + URLEncoder.encode(LED3Helper, "UTF-8")
                    + "&" + URLEncoder.encode("HoverFlag", "UTF-8") + "=" + URLEncoder.encode(hoverHelper, "UTF-8")
                    + "&" + URLEncoder.encode("CameraFlag", "UTF-8") + "=" + URLEncoder.encode(cameraHelper, "UTF-8")
                    + "&" + URLEncoder.encode("rgb1", "UTF-8") + "=" + URLEncoder.encode(buoy.getRGB1(), "UTF-8")
                    + "&" + URLEncoder.encode("rgb2", "UTF-8") + "=" + URLEncoder.encode(buoy.getRGB2(), "UTF-8")
                    + "&" + URLEncoder.encode("rgb3", "UTF-8") + "=" + URLEncoder.encode(buoy.getRGB3(), "UTF-8");


            Log.i("data are", data);

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();

            outputStream.close();


            InputStream inputstream = httpURLConnection.getInputStream();

            StringBuilder result = inputToString(inputstream);

            Log.i("response", result.toString());
            return result.toString();
        } catch (MalformedURLException e) {
            Log.i("MalformedURL", e.toString());
        } catch (IOException e) {
            flagIOException = true;
            Log.i("IOException", e.toString());
        }
        return null;
    }

    // sunartisi gia download twn shmadourwn
    public List<BuoyClass> downloadBuoys() {
        List<BuoyClass> listWithBuoys = new ArrayList<>();

        String urL = selectAllURL;

        try {
            //ekteloume http request
            URL url = new URL(urL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);

            InputStream inputstream = httpURLConnection.getInputStream();

            //metatropi se string builder to response
            StringBuilder result = inputToString(inputstream);
            Log.i("response", result.toString());

            // to response einai se json morfi
            JSONObject jsonResponse = new JSONObject(result.toString());
            JSONArray jsonMainNode = jsonResponse.optJSONArray("buoy");

            // boh8itikes metablites
            String id, latitude, longitude, orientation, led1, led2, led3, rgb1, rgb2, rgb3, targetLat, targetLng, hover, camera;
//json parsing
            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONArray jsonArray = jsonMainNode.getJSONArray(i);
                id = jsonArray.getString(0);
                latitude = jsonArray.getString(1);
                longitude = jsonArray.getString(2);
                orientation = jsonArray.getString(3);
                led1 = jsonArray.getString(4);
                led2 = jsonArray.getString(5);
                led3 = jsonArray.getString(6);
                rgb1 = jsonArray.getString(7);
                rgb2 = jsonArray.getString(8);
                rgb3 = jsonArray.getString(9);
                targetLat = jsonArray.getString(10);
                targetLng = jsonArray.getString(11);
                hover = jsonArray.getString(12);
                camera = jsonArray.getString(13);

                // populate thn lista me tis simadoures
                listWithBuoys.add(new BuoyClass(Integer.parseInt(id), Integer.parseInt(orientation), Double.parseDouble(latitude),
                        Double.parseDouble(longitude), Double.parseDouble(targetLat), Double.parseDouble(targetLng),
                        stringToBoolean(led1), stringToBoolean(led2), stringToBoolean(led3),
                        stringToBoolean(hover), stringToBoolean(camera), rgb1, rgb2, rgb3, getApplicationContext()));
            }
            // lista me tis simadoures
            return listWithBuoys;

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            flagIOException = true;
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // metatroph tou response se StringBuilder
    private StringBuilder inputToString(InputStream input) {
        String line;

        StringBuilder answer = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(input));

        try {
            while ((line = br.readLine()) != null) {
                answer.append(line);
            }
        } catch (Exception e) {
            Log.i("Error on inputtoStr: ", e.toString());
        }
        return answer;
    }

    // sunarthsh gia na metatrepei ta 0-1 apo string se true h false
    private boolean stringToBoolean(String word) {
        if (word.equals("0")) {
            return false;
        }
        return true;
    }
}
