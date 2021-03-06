/*
 * 
 * function to set throttle and steering manually
 */

void driveBuoy(){
    servoMotor.write(Buoy.steering);

    if(Buoy.throttle!=0){
        Serial.println( "DRIVING BUOYYYYYYYYYYYYYYY==============================================================" );
        // always stop motors briefly before abrupt changes
        if(previousThrottle != Buoy.throttle){
          digitalWrite( MOTOR_B_DIR, LOW );
          digitalWrite( MOTOR_B_PWM, LOW );
        }
        delay(100);
        previousThrottle = Buoy.throttle;
        // set the motor speed and direction
        digitalWrite( MOTOR_B_DIR, HIGH ); // direction = forward
        analogWrite( MOTOR_B_PWM, 255 - Buoy.throttle*2.55 ); 

        delay(500);
    }else{
        
        digitalWrite( MOTOR_B_DIR, LOW );
        digitalWrite( MOTOR_B_PWM, LOW );
    }
}
