void AutoDriveBuoy(){
  
  if(calc_distance(Buoy.latitude, Buoy.longitude, Buoy.TargetLat, Buoy.TargetLng) < 0.010){
    stopMoving();
    
  }
  else{
    get_direction(Buoy.latitude, Buoy.longitude, Buoy.TargetLat, Buoy.TargetLng);
    forward();
 } 
}


float calc_distance(float flat1, float flon1, float flat2, float flon2){
 float diflon,diflat;
 float dist;
 float a;
 float b;
 int R = 6371000;
  
 flat1 = radians(flat1);
 flat2 = radians(flat2);

 diflat = radians((flat2)-(flat1));
 diflon = radians((flon2)-(flon1));   

 a = sin(diflat/2) * sin(diflat/2) +
     cos(flat1) * cos(flat2)*
     sin(diflon/2) * sin(diflon/2);

 b= 2*atan2(sqrt(a),sqrt(1-a));
 dist =b*R;
 
// a = sin(flat1)*sin(flat2);
// b = cos(flat1)*cos(flat2)*cos(diflon);
// dist = acos(a+b)*R;

Serial.print ("distance is :"); Serial.println (dist);
 return dist;
}

void get_direction(float lat1, float lon1, float lat2, float lon2){
 int currentAngle = Buoy.orientation; 
 int heading = calc_bearing(lat1,lon1,lat2,lon2);
// while(abs(currentAngle-heading) > 8){
//   { 
    if((currentAngle - heading < 0 && abs(currentAngle - heading) < 180) || (currentAngle - heading > 0 && abs(currentAngle - heading) > 180))
    { 
      Serial.println("mpikeee sto turn right");
      stopMoving();
      turnRight();
    }  
    else 
    { 
      Serial.println("mpikeee sto turn left");
      stopMoving();
      turnLeft();
    }
    currentAngle = Buoy.orientation;
    heading = calc_bearing(lat1,lon1,lat2,lon2);
    Serial.print("Heading : ");Serial.print(heading);Serial.print(" , Current angle : ");Serial.print(currentAngle);
    Serial.print(" Angle difference:"); Serial.println(abs(currentAngle-heading));
//   }
// stopMoving();
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

//  if(heading < 0){heading=360+calc_heading(flat1,flon1,flat2,flon2); }
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
  Serial.println("FORWARD ??????");
   digitalWrite( MOTOR_B_DIR, LOW );
   digitalWrite( MOTOR_B_PWM, LOW );

   // set the motor speed and direction
   digitalWrite( MOTOR_B_DIR, HIGH ); // direction = forward
   analogWrite( MOTOR_B_PWM, 50); 
}

void turnRight(){

    servoMotor.write(135);
    forward();
//    delay(10000);

}

void turnLeft(){
    servoMotor.write(45);
    forward();
//    delay(10000);
}

