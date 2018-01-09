void mqttPublish(){
  
      Serial.println ( client.publish("BuoyConnected",Buoy.id));  
}


//Callback function of MQTT Subscription
void callback(char* topic, byte* payload, unsigned int length) {
    String response= "";
//if message send on this topic we have parse throttle and steering    
if(strcmp(topic,"Buoy2Drive")==0){

    Serial.println("DRIVE");
    for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
    response += (char) payload[i];
  }
  parseMqttDataDrive(response);
}
//if message send on this topic we have to parse general information of Buoy
if(strcmp(topic,"Buoy2")==0){
  
  Serial.print("Message arrived in topic: ");
  Serial.println(topic);
 
  Serial.print("Message:");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
    response += (char) payload[i];
  }
 
  Serial.println();
  Serial.println("-----------------------");
parseMqttData(response,counterMQTT);
   response ="";
//  payload=null;
  counterMQTT++;
  if(counterMQTT ==14) counterMQTT=0;

  Serial.print("Counter is ");
  Serial.print(counterMQTT);
  Serial.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); 
  }
}

void parseMqttDataDrive(String response){

  Serial.println(response.substring(0,response.indexOf('/')));
  String helper = response.substring(0,response.indexOf('/'));
  Serial.println(helper.substring(helper.indexOf(':')+1,helper.length()));
  Buoy.throttle = helper.substring(helper.indexOf(':')+1,helper.length()).toInt();
  Serial.println(Buoy.throttle);
  Serial.println("---------------------------------");

  Serial.println(response.substring(response.lastIndexOf('/'),response.length()));

  helper = response.substring(response.lastIndexOf('/'),response.length());
  Serial.println(helper.substring(helper.indexOf(':')+1,helper.length()));
  Buoy.steering = helper.substring(helper.indexOf(':')+1,helper.length()).toInt();

  Serial.println(Buoy.steering);
}
 void parseMqttData(String response,int counter){

  if(counter==0){// einai to id ths shmadouras 
    //den xreiazetai na to krathsoume
  }else if(counter==1){// to latitude pou exei o server
    Serial.println(response);
    Buoy.latitude=response.substring(response.lastIndexOf(':')+1,response.length()).toFloat();
    Serial.println(Buoy.latitude);
  }else if(counter==2){//to longitude pou exei o server
    Serial.println(response);
    Buoy.longitude=response.substring(response.lastIndexOf(':')+1,response.length()).toFloat();
    Serial.println(Buoy.longitude);
  }else if(counter==3){// to orientation pou exei o server
    Serial.println(response);
    Buoy.orientation=response.substring(response.lastIndexOf(':')+1,response.length()).toInt();
    Serial.println(Buoy.orientation);
  }else if(counter==4){// to LED1...... 1 gia anoixto 0 gia kleisto
    Serial.println(response);
    Buoy.LED1=response.substring(response.lastIndexOf(':')+1,response.length()).toInt();
    Serial.println(Buoy.LED1);
  }else if(counter==5){// to LED2 ......... 1 gia anoixto 0 gia kleisto
    Serial.println(response);
    Buoy.LED2=response.substring(response.lastIndexOf(':')+1,response.length()).toInt();
    Serial.println(Buoy.LED2);
  }else if(counter==6){// to LED3 ...... 1 gia anoixto 0 gia kleisto
    Serial.println(response);
    Buoy.LED3=response.substring(response.lastIndexOf(':')+1,response.length()).toInt();
    Serial.println(Buoy.LED3);
  }else if(counter==7){// to RGB1 timh.... sthn morfh #(kai kapoio hex)
    Serial.println(response);
    Buoy.RGB1=response.substring(response.lastIndexOf(':')+1,response.length());
    Serial.println(Buoy.RGB1);
  }else if(counter==8){// to RGB2 timh.... sthn morfh #(kai kapoio hex)
    Serial.println(response);
    Buoy.RGB2=response.substring(response.lastIndexOf(':')+1,response.length());
    Serial.println(Buoy.RGB2);
  }else if(counter==9){// to RGB3 timh.... sthn morfh #(kai kapoio hex)
    Serial.println(response);
    Buoy.RGB3=response.substring(response.lastIndexOf(':')+1,response.length());
    Serial.println(Buoy.RGB3);
  }else if(counter==10){// to target Lat gia to pou na paei h shmadoura
    Serial.println(response);
    Buoy.TargetLat=response.substring(response.lastIndexOf(':')+1,response.length()).toFloat();
    Serial.println(Buoy.TargetLat);
  }else if(counter==11){// to target Lng gia to pou na paei h shmadoura
    Serial.println(response);
    Buoy.TargetLng=response.substring(response.lastIndexOf(':')+1,response.length()).toFloat();
    Serial.println(Buoy.TargetLng);
  }else if(counter==12){// to hover flag mas..... 1 gia na kanei hover 0 gia na mjn kanei
    Serial.println(response);
    Buoy.hover=response.substring(response.lastIndexOf(':')+1,response.length()).toInt();
    Serial.println(Buoy.hover);
  }else if(counter==13){// to camera flag mas 1 gia na stelnei rtsp 0 gia na mhn stelnei
    Serial.println(response);
    Buoy.camera=response.substring(response.lastIndexOf(':')+1,response.length()).toInt();
    Serial.println(Buoy.camera);
  }
}
