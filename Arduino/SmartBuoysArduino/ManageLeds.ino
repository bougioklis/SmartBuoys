void manageLEDS(){
  //Open or Close Led depends on Buoy
   if(Buoy.LED1==1){
    digitalWrite(led,HIGH);
   }else{
    digitalWrite(led,LOW);
   }
   
   if(Buoy.LED2==1){
    digitalWrite(led1,HIGH);
   }else{
    digitalWrite(led1,LOW);
   }
   if(Buoy.LED3==1){
    digitalWrite(led2,HIGH);
   }else{
    digitalWrite(led2,LOW);
   }
}
