// publish on specific topic that the buoy is online.
// then our server will publish its information
void mqttPublish() {
  client.publish("BuoyConnected", Buoy.id);
}

// publish on mqtt latitude longitude and orientation
void updateServerLatLngOrientation() {

  String dataString = "Orientation:";
  dataString.concat(Buoy.orientation);
  dataString += "**Lat:";
  dataString.concat(Buoy.latitude);
  dataString += "**Lng:";
  dataString.concat(Buoy.longitude);
  dataString += "**ID:";
  dataString.concat(Buoy.id);

  char charData[55];
  dataString.toCharArray(charData, 55);

  client.publish("Buoy2Update", charData);
}


//Callback function of MQTT Subscription
void callback(char* topic, byte* payload, unsigned int length) {
  String response = "";
  //if message send on this topic we have to parse throttle and steering
  if (strcmp(topic, "Buoy2Drive") == 0) {

    Serial.println("DRIVE");
    for (int i = 0; i < length; i++) {
      Serial.print((char)payload[i]);
      response += (char) payload[i];
    }
    parseMqttDataDrive(response);
  }
  //if message send on this topic we have to parse general information of Buoy
  if (strcmp(topic, "Buoy2") == 0) {

    Serial.print("Message:");
    for (int i = 0; i < length; i++) {
      Serial.print((char)payload[i]);
      response += (char) payload[i];
    }

    parseMqttData(response, counterMQTT);
    response = "";

    counterMQTT++;
    if (counterMQTT == 14) {
      counterMQTT = 0;
      finishedParsing = true;
    }
  }
}

void parseMqttDataDrive(String response) {

  String helper = response.substring(0, response.indexOf('/'));
  Buoy.throttle = helper.substring(helper.indexOf(':') + 1, helper.length()).toInt();

  helper = response.substring(response.lastIndexOf('/'), response.length());
  Serial.println(helper.substring(helper.indexOf(':') + 1, helper.length()));
  Buoy.steering = helper.substring(helper.indexOf(':') + 1, helper.length()).toInt();
}
void parseMqttData(String response, int counter) {

  if (counter == 0) { // einai to id ths shmadouras
    //den xreiazetai na to krathsoume
  } else if (counter == 1) { // to latitude pou exei o server

    Buoy.latitude = response.substring(response.lastIndexOf(':') + 1, response.length()).toFloat();
  } else if (counter == 2) { //to longitude pou exei o server

    Buoy.longitude = response.substring(response.lastIndexOf(':') + 1, response.length()).toFloat();
  } else if (counter == 3) { // to orientation pou exei o server

    Buoy.orientation = response.substring(response.lastIndexOf(':') + 1, response.length()).toInt();

  } else if (counter == 4) { // to LED1...... 1 gia anoixto 0 gia kleisto

    Buoy.LED1 = response.substring(response.lastIndexOf(':') + 1, response.length()).toInt();

  } else if (counter == 5) { // to LED2 ......... 1 gia anoixto 0 gia kleisto

    Buoy.LED2 = response.substring(response.lastIndexOf(':') + 1, response.length()).toInt();

  } else if (counter == 6) { // to LED3 ...... 1 gia anoixto 0 gia kleisto

    Buoy.LED3 = response.substring(response.lastIndexOf(':') + 1, response.length()).toInt();

  } else if (counter == 7) { // to RGB1 timh.... sthn morfh #(kai kapoio hex)

    Buoy.RGB1 = response.substring(response.lastIndexOf(':') + 1, response.length());

  } else if (counter == 8) { // to RGB2 timh.... sthn morfh #(kai kapoio hex)

    Buoy.RGB2 = response.substring(response.lastIndexOf(':') + 1, response.length());

  } else if (counter == 9) { // to RGB3 timh.... sthn morfh #(kai kapoio hex)

    Buoy.RGB3 = response.substring(response.lastIndexOf(':') + 1, response.length());

  } else if (counter == 10) { // to target Lat gia to pou na paei h shmadoura

    Buoy.TargetLat = response.substring(response.lastIndexOf(':') + 1, response.length()).toFloat();
    Serial.println(Buoy.TargetLat, 6);
  } else if (counter == 11) { // to target Lng gia to pou na paei h shmadoura

    Buoy.TargetLng = response.substring(response.lastIndexOf(':') + 1, response.length()).toFloat();
    Serial.println(Buoy.TargetLng, 6);
  } else if (counter == 12) { // to hover flag mas..... 1 gia na kanei hover 0 gia na mjn kanei

    Buoy.hover = response.substring(response.lastIndexOf(':') + 1, response.length()).toInt();

  } else if (counter == 13) { // to camera flag mas 1 gia na stelnei rtsp 0 gia na mhn stelnei

    Buoy.camera = response.substring(response.lastIndexOf(':') + 1, response.length()).toInt();
  }
}
