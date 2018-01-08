#include <SPI.h>
#include <Ethernet.h>
#include <PubSubClient.h>
#include <Servo.h>
#include <avr/wdt.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_HMC5883_U.h>
#include <math.h>


/* Assign a unique ID to this sensor at the same time */
Adafruit_HMC5883_Unified mag = Adafruit_HMC5883_Unified(12345);

// wired connections for DC MOTOR
#define HG7881_B_IA 5 // D5 --> Motor B Input A --> MOTOR B +
#define HG7881_B_IB 6 // D6 --> Motor B Input B --> MOTOR B -
 
// functional connections
#define MOTOR_B_PWM HG7881_B_IA // Motor B PWM Speed
#define MOTOR_B_DIR HG7881_B_IB // Motor B Direction
 
// the actual values for "fast" and "slow" depend on the motor
#define PWM_SLOW 50  // arbitrary slow speed PWM duty cycle   EDW NA ALLAKSOUME TIS TIMES
#define PWM_FAST 200 // arbitrary fast speed PWM duty cycle   EDW NA ALLAKSOUME TIS TIMES
#define DIR_DELAY 1000 // brief delay for abrupt motor changes

// assign a MAC address for the ethernet controller.
// fill in your address here:
byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED
};
// fill in an available IP address on your network here,
// for manual configuration:
IPAddress ip(192, 168, 0, 3);

// fill in your Domain Name Server address here:
IPAddress myDns(1, 1, 1, 1);

// initialize the library instance:
EthernetClient Etheclient;

//IPAddress server(192,168,1,8);
 Servo servoMotor; 

//MQTT server and port
const char* mqttServer = "192.168.0.25";
const int mqttPort = 1883;

PubSubClient client(Etheclient);


int led=2,led1=3,led2=4; // led pins
int servoPin=9;// motorServo pin
int counter=0;//counter gia poio mqtt stal8ike

//struck gia na kratame tis plhrofories
struct Buoy{
  String id="id=2";
  float latitude,longitude,TargetLat,TargetLng;
  int orientation;
  int LED1,LED2,LED3;
  String RGB1,RGB2,RGB3;
  int hover,camera;

  int throttle=0,steering=0;
  
} Buoy;
struct Buoy Buoy2;


void callback(char* topic, byte* payload, unsigned int length) {
    String response= "";
if(strcmp(topic,"Buoy2Drive")==0){

    Serial.println("DRIVE");
    for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
    response += (char) payload[i];
  }
  parseMqttDataDrive(response);
}
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
parseMqttData(response,counter);
   response ="";
//  payload=null;
  counter++;
  if(counter ==14) counter=0;

  Serial.print("Counter is ");
  Serial.print(counter);
  Serial.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); 
  }
}

void parseMqttDataDrive(String response){

  Serial.println(response.substring(0,response.indexOf('/')));
  String helper = response.substring(0,response.indexOf('/'));
  Serial.println(helper.substring(helper.indexOf(':')+1,helper.length()));
Buoy2.throttle = helper.substring(helper.indexOf(':')+1,helper.length()).toInt();
Serial.println(Buoy2.throttle);
  Serial.println("---------------------------------");

  Serial.println(response.substring(response.lastIndexOf('/'),response.length()));

   helper = response.substring(response.lastIndexOf('/'),response.length());
      Serial.println(helper.substring(helper.indexOf(':')+1,helper.length()));
Buoy2.steering = helper.substring(helper.indexOf(':')+1,helper.length()).toInt();

Serial.println(Buoy2.steering);
}
 void parseMqttData(String response,int counter){

  if(counter==0){// einai to id ths shmadouras 
    //den xreiazetai na to krathsoume
  }else if(counter==1){// to latitude pou exei o server
    Serial.println(response);
            Buoy2.latitude=response.substring(response.lastIndexOf(':')+1,response.length()).toFloat();
    Serial.println(Buoy2.latitude);
  }else if(counter==2){//to longitude pou exei o server
        Serial.println(response);
            Buoy2.longitude=response.substring(response.lastIndexOf(':')+1,response.length()).toFloat();
    Serial.println(Buoy2.longitude);
  }else if(counter==3){// to orientation pou exei o server
        Serial.println(response);
            Buoy2.orientation=response.substring(response.lastIndexOf(':')+1,response.length()).toInt();
    Serial.println(Buoy2.orientation);
  }else if(counter==4){// to LED1...... 1 gia anoixto 0 gia kleisto
        Serial.println(response);
            Buoy2.LED1=response.substring(response.lastIndexOf(':')+1,response.length()).toInt();
    Serial.println(Buoy2.LED1);
  }else if(counter==5){// to LED2 ......... 1 gia anoixto 0 gia kleisto
        Serial.println(response);
            Buoy2.LED2=response.substring(response.lastIndexOf(':')+1,response.length()).toInt();
    Serial.println(Buoy2.LED2);
  }else if(counter==6){// to LED3 ...... 1 gia anoixto 0 gia kleisto
        Serial.println(response);
            Buoy2.LED3=response.substring(response.lastIndexOf(':')+1,response.length()).toInt();
    Serial.println(Buoy2.LED3);
  }else if(counter==7){// to RGB1 timh.... sthn morfh #(kai kapoio hex)
        Serial.println(response);
            Buoy2.RGB1=response.substring(response.lastIndexOf(':')+1,response.length());
    Serial.println(Buoy2.RGB1);
  }else if(counter==8){// to RGB2 timh.... sthn morfh #(kai kapoio hex)
        Serial.println(response);
            Buoy2.RGB2=response.substring(response.lastIndexOf(':')+1,response.length());
    Serial.println(Buoy2.RGB2);
  }else if(counter==9){// to RGB3 timh.... sthn morfh #(kai kapoio hex)
        Serial.println(response);
            Buoy2.RGB3=response.substring(response.lastIndexOf(':')+1,response.length());
    Serial.println(Buoy2.RGB3);
  }else if(counter==10){// to target Lat gia to pou na paei h shmadoura
        Serial.println(response);
            Buoy2.TargetLat=response.substring(response.lastIndexOf(':')+1,response.length()).toFloat();
    Serial.println(Buoy2.TargetLat);
  }else if(counter==11){// to target Lng gia to pou na paei h shmadoura
        Serial.println(response);
            Buoy2.TargetLng=response.substring(response.lastIndexOf(':')+1,response.length()).toFloat();
    Serial.println(Buoy2.TargetLng);
  }else if(counter==12){// to hover flag mas..... 1 gia na kanei hover 0 gia na mjn kanei
        Serial.println(response);
            Buoy2.hover=response.substring(response.lastIndexOf(':')+1,response.length()).toInt();
    Serial.println(Buoy2.hover);
  }else if(counter==13){// to camera flag mas 1 gia na stelnei rtsp 0 gia na mhn stelnei
        Serial.println(response);
            Buoy2.camera=response.substring(response.lastIndexOf(':')+1,response.length()).toInt();
    Serial.println(Buoy2.camera);
  }
}



void setup() {
   wdt_disable();
   wdt_enable(WDTO_8S);

  // put your setup code here, to run once:

   Serial.begin(9600);
 
   delay(1000);

   /* Initialise the sensor */
  if(!mag.begin())
  {
    /* There was a problem detecting the HMC5883 ... check your connections */
    Serial.println("Ooops, no HMC5883 detected ... Check your wiring!");
    while(1);
  }
  // start the Ethernet connection using a fixed IP address and DNS server:
  Ethernet.begin(mac, ip, myDns);
  // print the Ethernet board/shield's IP address:
  Serial.print("My IP address: ");
  Serial.println(Ethernet.localIP());
  
  client.setServer(mqttServer, mqttPort);// 8etoume sto MQTT to server to port kai to callback
  client.setCallback(callback);
 
  while (!client.connected()) {
    Serial.println("Connecting to MQTT...");
 
    if (client.connect("Client2")) {// to Client2 einai ena client id wste na kseroume poios akouei h stelnei
 
      Serial.println("connected");  
 
    } else {
 
      Serial.print("failed with state ");
      Serial.print(client.state());
      delay(2000);
 
    }
  }
      client.subscribe("Buoy2Drive");
     client.subscribe("Buoy2");// subscribe sto sugkekimeno topic
  servoMotor.attach(servoPin);
  servoMotor.write(90);
  

   pinMode(led,OUTPUT); 
   pinMode(led1,OUTPUT);
   pinMode(led2,OUTPUT);

   pinMode( MOTOR_B_DIR, OUTPUT );
   pinMode( MOTOR_B_PWM, OUTPUT );
   digitalWrite( MOTOR_B_DIR, LOW );
   digitalWrite( MOTOR_B_PWM, LOW );
}

void loop() {
  // put your main code here, to run repeatedly:
  if(client.loop()){
    Serial.println("Client Connected Success");
    wdt_reset();
  }else{
    Serial.println("Shit");
  }
  manageLEDS();
  driveBuoy();
  getHeading();

  Serial.println(Buoy2.throttle);
//  Serial.println(Buoy2.longitude);
//  Serial.println(Buoy2.TargetLat);
//  Serial.println(Buoy2.TargetLng);
  
  if(Buoy2.latitude!= Buoy2.TargetLat || Buoy2.longitude != Buoy2.TargetLng){
    driveBuoyAutomatically();
  }
  
}

void manageLEDS(){
  //analoga me tis times anoigoume h kleinoume ta LED
   if(Buoy2.LED1==1){
    Serial.print("fwsanoikse");
      digitalWrite(led,HIGH);

   }else{
    Serial.println("fwskleise");
      digitalWrite(led,LOW);
   }
   
 if(Buoy2.LED2==1){
    Serial.print("fwsanoikse");
      digitalWrite(led1,HIGH);

   }else{
    Serial.println("fwskleise");
      digitalWrite(led1,LOW);
   }
    if(Buoy2.LED3==1){
    Serial.print("fwsanoikse");
      digitalWrite(led2,HIGH);

   }else{
    Serial.println("fwskleise");
      digitalWrite(led2,LOW);
   }
}



void driveBuoy(){
    servoMotor.write(Buoy2.steering);

if(Buoy2.throttle!=0){
  
  Serial.println( "DRIVING BUOYYYYYYYYYYYYYYY==============================================================" );
        // always stop motors briefly before abrupt changes
      
        digitalWrite( MOTOR_B_DIR, LOW );
        digitalWrite( MOTOR_B_PWM, LOW );
//        delay( DIR_DELAY );
        // set the motor speed and direction
        digitalWrite( MOTOR_B_DIR, HIGH ); // direction = forward
        analogWrite( MOTOR_B_PWM, 255 - Buoy2.throttle*2.55 ); // PWM speed = slow

  delay(500);
}
if(Buoy2.throttle==0){
  digitalWrite( MOTOR_B_DIR, LOW );
  digitalWrite( MOTOR_B_PWM, LOW );
}
}

void getHeading(){
 /* Get a new sensor event */ 
  sensors_event_t event; 
  mag.getEvent(&event);

   // Hold the module so that Z is pointing 'up' and you can measure the heading with x&y
  // Calculate heading when the magnetometer is level, then correct for signs of axis.
  float heading = atan2(event.magnetic.y, event.magnetic.x);
  
  // Once you have your heading, you must then add your 'Declination Angle', which is the 'Error' of the magnetic field in your location.
  // Find yours here: http://www.magnetic-declination.com/
  // Mine is: -13* 2' W, which is ~13 Degrees, or (which we need) 0.22 radians
  // If you cannot find your Declination, comment out these two lines, your compass will be slightly off.
  float declinationAngle = 0.22;
  heading += declinationAngle;
  
  // Correct for when signs are reversed.
  if(heading < 0)
    heading += 2*PI;
    
  // Check for wrap due to addition of declination.
  if(heading > 2*PI)
    heading -= 2*PI;
   
  // Convert radians to degrees for readability.
  float headingDegrees = heading * 180/M_PI; 
  
  Serial.print("Heading (degrees): "); Serial.println(headingDegrees);
  
  delay(500);
}

void driveBuoyAutomatically(){
  double angle = angleFromCoordinate( Buoy2.latitude,Buoy2.longitude,Buoy2.TargetLat,Buoy2.TargetLng);
  Serial.println(angle);
}

double angleFromCoordinate(double lat1, double long1, double lat2,
        double long2) {

    double dLon = (long2 - long1);

    double y = sin(dLon) *cos(lat2);
    double x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon);

    double brng = atan2(y, x);

    //brng = math.toDegrees(brng);
    brng =brng * 57296 / 1000;

    brng = ((int)brng + 360) % 360;
    brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise

    return brng;
}



