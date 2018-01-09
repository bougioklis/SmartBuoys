<?php
/*
*Created by Bougioklis George
*
* Subscribe to AllBuoys Topics and update  Orientation Latitutde and Longtitude
* of each Buoy
*/

error_reporting(E_ALL);

include_once "phpMQTT.php";
require 'Init.php';

$server = "192.168.1.1";     // change if necessary
$port = 1883;                     // change if necessary
$username = "";                   // set your username
$password = "";                   // set your password
$client_id = "phpMQTTUpdater"; // make sure this is unique for connecting to sever - you could use uniqid()

$mqtt = new phpMQTT($server, $port, $client_id);

if(!$mqtt->connect(true, NULL, $username, $password)) {
	exit(1);
}
//select all IDs
$sql = "SELECT ID FROM Buoy ";
$result =$conn -> query($sql);

if($result){
	$buoy =array();
	while($row =$result ->fetch_assoc()){
		//put every id  on buoy array
		array_push($buoy,$row[ID]);
	}
}

//show buoys later delete
var_dump($buoy);

//foreach id on buoy array subscribe to topic "Buoy(id)Update"
foreach($buoy as $i){
	$topic['Buoy'.$i.'Update'] = array("qos" => 0, "function" => "procmsg");
	$mqtt->subscribe($topic,0);
	//unset the topic array to enter new  values
	unset ($topics);
}

while($mqtt->proc()){

}
$mqtt->close();

function procmsg($topic, $msg){

	$conn= new mysqli ("localhost","bougioklis","raspberry","smart_buoy");
	if($conn -> connect_error){
		die("Connections failed: ". $conn -> connect_error);
	}


	$msgParts = explode("**",$msg);
	//msg parts[0] -> orientation
	//msg parts[1] -> Latitude
	//msg parts[2] -> longitude
	//msg parts[3] -> id

	//has a string like orientation:(int)
	$orientationParts = explode(":",$msgParts[0]);
	$orientation=$orientationParts[1];// has the actual orientation
	echo $orientation;

	//has a string like Lat:(float)
	$LatParts =explode(":",$msgParts[1]);
	$Lat = $LatParts[1];//has the actual latitude
	echo $Lat;

	//has a string like Lng:(float)
	$LngParts = explode(":",$msgParts[2]);
	$Lng = $LngParts[1];// has the actual longitude
	echo $Lng;

	//has a string like ID:(int)
	$idParts =explode(":",$msgParts[3]);
	$id = $idParts[1];//has the actual id

	$sql = "UPDATE Buoy SET orientation=?,latitude=?,longitude=? WHERE ID =?" ;
	if($stmt = $conn -> prepare($sql)){
		$stmt ->bind_param("iddi",$orientation,$Lat,$Lng,$id);
		$stmt -> execute();
		echo "<br>". $stmt -> error;
		if ($stmt -> errno){
			echo "success";
		}else{
			echo "failure";
		}
	}
}
