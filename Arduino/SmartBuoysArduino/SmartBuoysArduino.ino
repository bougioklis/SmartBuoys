#include <SPI.h>
#include <Ethernet.h>
#include <PubSubClient.h>
#include <Servo.h>
#include <avr/wdt.h>
#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_HMC5883_U.h>
#include <math.h>


/*Initializa & Assign a unique ID to this compass sensor at the same time */
Adafruit_HMC5883_Unified mag = Adafruit_HMC5883_Unified(12345);

// wired connections for DC MOTOR
#define HG7881_B_IA 5 // D5 --> Motor B Input A --> MOTOR B +
#define HG7881_B_IB 6 // D6 --> Motor B Input B --> MOTOR B -

// functional connections
#define MOTOR_B_PWM HG7881_B_IA // Motor B PWM Speed
#define MOTOR_B_DIR HG7881_B_IB // Motor B Direction

// assign a MAC address for the ethernet controller.
// fill in your address here:
byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED
};

// Available IP address on  network here,
// for manual configuration:
IPAddress ip(192, 168, 1, 3);

// fill in your Domain Name Server address here:
IPAddress myDns(1, 1, 1, 1);

// initialize the EthernetClient instance:
EthernetClient Etheclient;

//Servo motor instance
Servo servoMotor; 

//MQTT server and port
const char* mqttServer = "192.168.1.1";
const int mqttPort = 1883;

//MQTT client
PubSubClient client(Etheclient);

int led=2,led1=3,led2=4; // led pins
int servoPin=9;// motorServo pin
int counterMQTT=0;//counter for Mqtt (Which Mqtt was send)
String clientMQTT="Buoy2";// Client id for MQTT protocol

//struck to keep Buoy's informations
struct BuoyStruck{
  const char *id="id=2";
  float latitude,longitude,TargetLat,TargetLng;
  int orientation;
  int LED1,LED2,LED3;
  String RGB1,RGB2,RGB3;
  int hover,camera;

  const char *generalTopic = "Buoy2";
  const char *drivingTopic="Buoy2Drive";
  int throttle=0,steering=0;
  
} BuoyStruck;

//Struck's instance
struct BuoyStruck Buoy;

boolean IsfirstLoop =true;

void setup() {
  // put your setup code here, to run once:

  //warchdog initialize
  wdt_disable();
  wdt_enable(WDTO_8S);

  //Start Serial Monitor
  Serial.begin(9600);
  delay(1000);

  //initialize Compass sensor
  if(!mag.begin()){
    /* There was a problem detecting the HMC5883 ... check your connections */
    Serial.println("Ooops, no HMC5883 detected ... Check your wiring!");
    while(1);
  }

   // start the Ethernet connection using a fixed IP address and DNS server:
  Ethernet.begin(mac, ip, myDns);
  // print the Ethernet board/shield's IP address:
  Serial.print("My IP address: ");
  Serial.println(Ethernet.localIP());

  //Start mqtt protocol with the given server and port form GlobalVars
  client.setServer(mqttServer, mqttPort);
  //Set mqtt callback function
  client.setCallback(callback);

  // run while client is not Connected to MQTT SERVER
  while (!client.connected()) {
    Serial.println("Connecting to MQTT...");
    // MQTT Client connection
    if (client.connect("ArduinoClient")) {
 
      Serial.println("connected");  
 
    } else {
      //error code and retrying to connect
      Serial.print("failed with state ");
      Serial.print(client.state());
      delay(2000);
    }
  }

  //Client subscribe to necessary topics
  client.subscribe(Buoy.drivingTopic);
  client.subscribe(Buoy.generalTopic);

  // pins mode
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
    
    //if client is connected to mqtt reset the watchdog Timer
    wdt_reset();
  }else{
    // if client cannot connect to mqtt for 8 seconds
    // the arduino will be rebooted
    Serial.println("Client Cannot Connect");
  }

  if(IsfirstLoop){
    mqttPublish();
    IsfirstLoop= false;
  }

  manageLEDS();
  driveBuoy();
  getHeading();
  
}
