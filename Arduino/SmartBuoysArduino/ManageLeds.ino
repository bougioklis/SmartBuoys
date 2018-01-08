void manageLEDS(){
  //Open or Close Led depends on Buoy
   if(Buoy.LED1==1){
    Serial.print("fwsanoikse");
    digitalWrite(led,HIGH);
   }else{
    Serial.println("fwskleise");
    digitalWrite(led,LOW);
   }
   
   if(Buoy.LED2==1){
    Serial.print("fwsanoikse");
    digitalWrite(led1,HIGH);
   }else{
    Serial.println("fwskleise");
    digitalWrite(led1,LOW);
   }
   if(Buoy.LED3==1){
    Serial.print("fwsanoikse");
    digitalWrite(led2,HIGH);
   }else{
    Serial.println("fwskleise");
    digitalWrite(led2,LOW);
   }
}
