<?php
/*
*
*Created By Bougioklis Giwrgos
*
*Update TargetLat and TargetLng on our DB
*/

require 'Init.php';
include_once 'publishMqtt.php';

//get vars

$id = $_POST['id'];

$lat = $_POST['targetLat'];
$lng = $_POST['targetLng'];

$sql = "UPDATE Buoy SET targetLat = ? , targetLng = ? WHERE ID = ?";

if($stmt = $conn -> prepare($sql)){
	$stmt -> bind_param("ssi",$lat,$lng,$id);
	$stmt -> execute();
	if($stmt -> affected_rows ){
		echo "1";
	}else{
		echo "-1";
	}
}

//calling mqtt function to alert Buoy about the changes
MQTTPublish("Buoy".$id,$id);
