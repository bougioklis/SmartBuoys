<?php
/*
*Created By Bougioklis George
*/
// require database connection file
require 'Init.php';

//Select  all Buoys
$sql = "SELECT * FROM Buoy";
$result = $conn -> query($sql);

if($result){
	$buoy = array();
	$finalResult = array();
	//while we have buoys  fetch results
	while ($row = $result -> fetch_assoc() ){
		$id   = $row["ID"];
		$lat  = $row["latitude"];
		$lng  = $row["longitude"];
		$orientation = $row["orientation"];
		$LED1  = $row["LED1"];
		$LED2  = $row["LED2"];
		$LED3  = $row["LED3"];
		$RGB1  = $row["RGB1"];
		$RGB2  = $row["RGB2"];
		$RGB3  = $row["RGB3"];
		$targetLat = $row["targetLat"];
		$targetLng = $row["targetLng"];
		$hover = $row["hoverFlag"];
		$camera = $row["cameraFlag"];

		// push results on an array
		array_push($buoy, $id);
		array_push($buoy, $lat);
		array_push($buoy, $lng);
		array_push($buoy, $orientation);
		array_push($buoy, $LED1);
		array_push($buoy, $LED2);
		array_push($buoy, $LED3);
		array_push($buoy, $RGB1);
		array_push($buoy, $RGB2);
		array_push($buoy, $RGB3);
		array_push($buoy, $targetLat);
		array_push($buoy, $targetLng);
		array_push($buoy, $hover);
		array_push($buoy, $camera);

		//push array for json encoding
		array_push($finalResult, $buoy);

		unset($buoy);
		$buoy= array();

	}

	//JSON encoding all buoys
	$json['buoy'] = $finalResult;
	echo json_encode($json);


}else{

	echo "-1";
}

?>

