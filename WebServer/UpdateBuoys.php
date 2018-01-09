/*
*Created By Bougioklis George
*/
<?php

/*
* Buoy Update
*/

require 'Init.php';
include_once 'testPublish.php';

//get buoy's vars

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

//calling mqtt publish function to  alert buoy about the  changes
MQTTPublish("Buoy".$id,$id);

?>
