void AutoDriveBuoy(){
  
  if(calc_distance(Buoy.latitude, Buoy.longitude, Buoy.TargetLat, Buoy.TargetLng) < 10){
    stopMoving();
    isAutoDriving=false;
  }
  else{
    get_direction(Buoy.latitude, Buoy.longitude, Buoy.TargetLat, Buoy.TargetLng);
 } 
}


float calc_distance(float flat1, float flon1, float flat2, float flon2){
float dist_calc=0;
float dist_calc2=0;
float diflat=0;
float diflon=0;

//I've to spplit all the calculation in several steps. If i try to do it in a single line the arduino will explode.
diflat=radians(flat2-flat1);
flat1=radians(flat1);
flat2=radians(flat2);
diflon=radians((flon2)-(flon1));

dist_calc = (sin(diflat/2.0)*sin(diflat/2.0));
dist_calc2= cos(flat1);
dist_calc2*=cos(flat2);
dist_calc2*=sin(diflon/2.0);
dist_calc2*=sin(diflon/2.0);
dist_calc +=dist_calc2;

dist_calc=(2*atan2(sqrt(dist_calc),sqrt(1.0-dist_calc)));

dist_calc*=6371e3; //Converting to meters


Serial.print ("distance is :"); Serial.println (dist_calc);
 return dist_calc;
}

void get_direction(float lat1, float lon1, float lat2, float lon2){
 int currentAngle = Buoy.orientation; 
 int bearing = calc_bearing(lat1,lon1,lat2,lon2);

//TODO na valoume kapoia apoklisi?
    if(currentAngle -bearing <0 ){ 
      Serial.println("mpikeee sto turn right");
      stopMoving();
      turnRight();
    }else if (currentAngle -bearing >0){ 
      Serial.println("mpikeee sto turn left");
      stopMoving();
      turnLeft();
    }else{
      forward();
    }
    currentAngle = Buoy.orientation;
    bearing = calc_bearing(lat1,lon1,lat2,lon2);
    Serial.print("bearing : ");Serial.print(bearing);Serial.print(" , Current angle : ");Serial.print(currentAngle);
    Serial.print(" Angle difference:"); Serial.println(abs(currentAngle-bearing));

}

int calc_bearing(float flat1, float flon1, float flat2, float flon2)
{
  float a;
  float b;
  float heading;
  float diflon;

  flat1 = radians(flat1);
  flat2 = radians(flat2);
  
  diflon = radians((flon2)-(flon1));
  a = sin(diflon)*cos(flat2);
  b = (cos(flat1)*sin(flat2)) -(sin(flat1)*cos(flat2)*cos(diflon));
  a = atan2(a,b);
  heading = degrees(a);

  heading = (heading+360.00);
  heading = fmod(heading,360);
 
  return heading;
}

void stopMoving(){
  digitalWrite( MOTOR_B_DIR, HIGH );
  digitalWrite( MOTOR_B_PWM, HIGH );

}

void forward(){

   delay(100);
   digitalWrite( MOTOR_B_DIR, LOW );
   digitalWrite( MOTOR_B_PWM, LOW );

   // set the motor speed and direction
   digitalWrite( MOTOR_B_DIR, HIGH ); // direction = forward
   analogWrite( MOTOR_B_PWM, 50); 
   delay(1000);
}

void turnRight(){

    servoMotor.write(135);
    forward();

}

void turnLeft(){
    servoMotor.write(45);
    forward();
}

