void driveBuoy(){
    servoMotor.write(Buoy.steering);

if(Buoy.throttle!=0){
  
  Serial.println( "DRIVING BUOYYYYYYYYYYYYYYY==============================================================" );
        // always stop motors briefly before abrupt changes
        digitalWrite( MOTOR_B_DIR, LOW );
        digitalWrite( MOTOR_B_PWM, LOW );

        // set the motor speed and direction
        digitalWrite( MOTOR_B_DIR, HIGH ); // direction = forward
        analogWrite( MOTOR_B_PWM, 255 - Buoy.throttle*2.55 ); 

  delay(500);
}
if(Buoy.throttle==0){
  digitalWrite( MOTOR_B_DIR, LOW );
  digitalWrite( MOTOR_B_PWM, LOW );
}
}
