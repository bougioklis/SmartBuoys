package com.example.bougioklis.smartbuoy.Classes;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.bougioklis.smartbuoy.R;

/**
 * Created by Bougioklis on 28-Oct-17.
 */

public class BuoyClass {

    //The unique Id of each Buoy and the orientation from the compass
    private int id,orientation;
    //lat,lng of the Buoy, targetLat,TargetLng oi the coordinates that the buoy has to go to
    private double lat,lng,targetLat,targetLng;

    // true or false
    private boolean led1,led2,led3,hoverflag,cameraflag;

    //rgbs on hex  if a rgb is equal to off then it is off.
    // orientationString = "βορειοδυτικα ktlp"
    //topic ID  is  "Buoy+ID"  for the MQTT protocol  and driveTopicID "Buoy+ID+Drive"
    private String RGB1,RGB2,RGB3,orientationString,topicID,driveTopicID;

    private Drawable markerIcon,selectedMarker;
    //default throttle and steering
    private int throttle=0,steering=90;

    public BuoyClass(int id, int orientation, double lat, double lng, double targetLat, double targetLng,
                     boolean led1, boolean led2, boolean led3, boolean hoverflag, boolean cameraflag, String RGB1,
                     String RGB2, String RGB3, Context ct) {
        this.id = id;
        this.orientation = orientation;
        this.lat = lat;
        this.lng = lng;
        this.targetLat = targetLat;
        this.targetLng = targetLng;
        this.led1 = led1;
        this.led2 = led2;
        this.led3 = led3;
        this.hoverflag = hoverflag;
        this.cameraflag = cameraflag;
        this.RGB1 = RGB1;
        this.RGB2 = RGB2;
        this.RGB3 = RGB3;

        this.topicID="Buoy"+this.id;
        this.driveTopicID="Buoy"+this.id+"Drive";

        calculateIcon(orientation,ct);
    }

    //function to calc based on ori the buoy's markericon and the orientationString
    private void calculateIcon(int orientation, Context ct){

        if((orientation>=0 && orientation<=22) || (orientation>=338 && orientation<=360)){
            //north
            this.markerIcon= ct.getResources().getDrawable(R.drawable.north);
            this.orientationString="Βορράς";
            this.selectedMarker=ct.getResources().getDrawable(R.drawable.selectednorth);
        }else if(orientation>=23 && orientation<=75){
            //northEast
            this.markerIcon= ct.getResources().getDrawable(R.drawable.northeast);
            this.orientationString="Βορρειο-Ανατολικά";
            this.selectedMarker=ct.getResources().getDrawable(R.drawable.selectednortheast);

        }else if(orientation>=76 && orientation<=112){
            //East
            this.markerIcon= ct.getResources().getDrawable(R.drawable.east);
            this.orientationString="Ανατολικά";
            this.selectedMarker=ct.getResources().getDrawable(R.drawable.selectedeast);

        }else if(orientation>=113 && orientation<=157){
            //SouthEast
            this.markerIcon= ct.getResources().getDrawable(R.drawable.southeast);
            this.orientationString="Νοτιο-Ανατολικά";
            this.selectedMarker=ct.getResources().getDrawable(R.drawable.selectedsoutheast);

        }else if(orientation>=158 && orientation<=202){
            //South
            this.markerIcon= ct.getResources().getDrawable(R.drawable.south);
            this.orientationString="Νότια";
            this.selectedMarker=ct.getResources().getDrawable(R.drawable.selectedsouth);

        }else if(orientation>=203 && orientation<=247){
            //SouthWest
            this.markerIcon= ct.getResources().getDrawable(R.drawable.southwest);
            this.orientationString="Νοτιο-Δυτικά";
            this.selectedMarker=ct.getResources().getDrawable(R.drawable.selectedsouthwest);

        }else if(orientation>=248 && orientation<=292){
            //west
            this.markerIcon= ct.getResources().getDrawable(R.drawable.west);
            this.orientationString="Δυτικά";
            this.selectedMarker=ct.getResources().getDrawable(R.drawable.selectedwest);

        }else if(orientation>=293 && orientation<=337){
            //northEast
            this.markerIcon= ct.getResources().getDrawable(R.drawable.northeast);
            this.orientationString="Βορειο-Δυτικά";
            this.selectedMarker=ct.getResources().getDrawable(R.drawable.selectednortheast);

        }
    }


    //getters & setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getTargetLat() {
        return targetLat;
    }

    public void setTargetLat(double targetLat) {
        this.targetLat = targetLat;
    }

    public double getTargetLng() {
        return targetLng;
    }

    public void setTargetLng(double targetLng) {
        this.targetLng = targetLng;
    }

    public boolean isLed1() {
        return led1;
    }

    public void setLed1(boolean led1) {
        this.led1 = led1;
    }

    public boolean isLed2() {
        return led2;
    }

    public void setLed2(boolean led2) {
        this.led2 = led2;
    }

    public boolean isLed3() {
        return led3;
    }

    public void setLed3(boolean led3) {
        this.led3 = led3;
    }

    public boolean isHoverflag() {
        return hoverflag;
    }

    public void setHoverflag(boolean hoverflag) {
        this.hoverflag = hoverflag;
    }

    public boolean isCameraflag() {
        return cameraflag;
    }

    public void setCameraflag(boolean cameraflag) {
        this.cameraflag = cameraflag;
    }

    public String getRGB1() {
        return RGB1;
    }

    public void setRGB1(String RGB1) {
        this.RGB1 = RGB1;
    }

    public String getRGB2() {
        return RGB2;
    }

    public void setRGB2(String RGB2) {
        this.RGB2 = RGB2;
    }

    public String getRGB3() {
        return RGB3;
    }

    public void setRGB3(String RGB3) {
        this.RGB3 = RGB3;
    }

    public Drawable getMarkerIcon() {
        return markerIcon;
    }

    public void setMarkerIcon(Drawable markerIcon) {
        this.markerIcon = markerIcon;
    }

    public String getOrientationString() {
        return orientationString;
    }

    public void setOrientationString(String orientationString) {
        this.orientationString = orientationString;
    }

    public Drawable getSelectedMarker() {
        return selectedMarker;
    }

    public void setSelectedMarker(Drawable selectedMarker) {
        this.selectedMarker = selectedMarker;
    }

    public String getTopicID() {
        return topicID;
    }

    public void setTopicID(String topicID) {
        this.topicID = topicID;
    }

    public String getDriveTopicID() {
        return driveTopicID;
    }

    public void setDriveTopicID(String driveTopicID) {
        this.driveTopicID = driveTopicID;
    }

    public int getThrottle() {
        return throttle;
    }

    public void setThrottle(int throttle) {
        this.throttle = throttle;
    }

    public int getSteering() {
        return steering;
    }

    public void setSteering(int steering) {
        this.steering = steering;
    }
}
