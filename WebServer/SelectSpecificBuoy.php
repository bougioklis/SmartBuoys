<?php

//Created by Bougioklis George

// Maybe we dont use this file anymore

//require database connection file
require 'Init.php';

//echo  $_SERVER['SERVER_ADDR'];

// specific buoy id 
$id=$_POST['id'];

//select everything from a buoy
$sql = "SELECT * FROM Buoy WHERE ID=?";

if($stmt = $conn -> prepare($sql)){

	$stmt -> bind_param("i",$id);
	$result = $stmt -> execute();
	$store=	$stmt -> get_result();

	if($result){

		$buoy = array();
		$finalResult = array();
		// fetch result
		$row = $store -> fetch_assoc();

			$id   = $row[ID];
			$lat  = $row[latitude];
			$lng  = $row[longitude];
			$orientation = $row[orientation];
			$LED1  = $row[LED1];
			$LED2  = $row[LED2];
			$LED3  = $row[LED3];
			$RGB1  = $row[RGB1];
			$RGB2  = $row[RGB2];
			$RGB3  = $row[RGB3];
			$targetLat = $row[targetLat];
			$targetLng = $row[targetLng];
			$hover = $row[hoverFlag];
			$camera = $row[cameraFlag];


		echo "ID:".$id."\n";
		echo "latitude:".$lat."\n";
		echo "longitude:".$lng."\n";
		echo "orientation:".$orientation."\n";
		echo "LED1:".$LED1."\n";
		echo "LED2:".$LED2."\n";
		echo "LED3:".$LED3."\n";
		echo "RGB1:".$RGB1."\n";
		echo "RGB2:".$RGB2."\n";
		echo "RGB3:".$RGB3."\n";
		echo "target Lat:".$targetLat."\n";
		echo "target Lng:".$targetLng."\n";
		echo "hover:".$hover."\n";
		echo "camera:".$camera."\n";


	}else{

		echo "-1";
	}
}
?>


