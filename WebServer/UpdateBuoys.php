<?php

require 'Init.php';
include_once 'testPublish.php';
/*
$id =1;

$led1 = 1;
$led2 = 0;
$led3 = 0;

$hover = 0;
$camera = 1;

$RGB1 = "off";
$RGB2 = "#ccff66";
$RGB3 = "#111a00";
*/

$id = $_POST['id'];

$led1 = $_POST['LED1'];
$led2 = $_POST['LED2'];
$led3 = $_POST['LED3'];

$hover = $_POST['HoverFlag'];
$camera = $_POST['CameraFlag'];

$RGB1 = $_POST['rgb1'];
$RGB2 = $_POST['rgb2'];
$RGB3 = $_POST['rgb3'];

$sql = "UPDATE Buoy SET LED1 = ? , LED2 = ? , LED3 = ? , hoverFlag = ? , cameraFlag =? , RGB1 = ? , RGB2 = ? , RGB3 = ? WHERE ID = ?";

if($stmt = $conn -> prepare($sql) ){
	$stmt -> bind_param("ssssssssi",$led1,$led2,$led3,$hover,$camera,$RGB1,$RGB2,$RGB3,$id);
	$stmt -> execute();
	if($stmt -> affected_rows){
		echo"1";
	}else{
		echo "-1";
	}
}

//kaloume tohn sunarthsh MQTTPUBLISH me topic to Buoy+id  tis shmadouras kai to id tis shmadouras
MQTTPublish("Buoy".$id,$id);

?>
