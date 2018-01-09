/*
*Created By Bougioklis George
*/
<?php

/*
*Subscibe to Topic BuoyConnected  when an arduino is powered on it will  publish on the specific topic and this script will
* call the publish function to publish arduino's data.
*/

//include php mqtt class
include_once "phpMQTT.php" ;

//include database connection file
require 'Init.php';

//include mqtt publish file
require 'testPublish.php';

//Our Server's ip
//$server = $_SERVER['SERVER_ADDR'];

$server = "192.168.1.1";     // change if necessary
$port = 1883;                     // change if necessary
$username = "";                   // set your username
$password = "";                   // set your password
$client_id = "phpMQTT-subscriber"; // make sure this is unique for connecting to sever - you could use uniqid()

// create Mqtt object
$mqttSub = new phpMQTT($server, $port, $client_id);

if(!$mqttSub->connect(true, NULL, $username, $password)) {
	exit(1);
}

//connect to topic BuoyConnected
$topics['BuoyConnected'] = array("qos" => 0, "function" => "procmsg");
$mqttSub->subscribe($topics, 0);
while($mqttSub->proc()){
		
}
$mqttSub->close();

//call back function
function procmsg($topic, $msg){
	// the msg is "id=(int)" 
	$buoy = explode("=" , $msg );
	$buoyId= $buoy[1];
	//publish on the specific topic
	MQTTPublish("Buoy".$buoyId,$buoyId);
}
