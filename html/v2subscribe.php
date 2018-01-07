<?php 

require ("phpMQTT.php");
require 'Init.php';

//$id = $_POST['id'];

$id =2;
$server= "192.168.1.3";
$port =1883;
$username = "";
$password = "";
$client_id= "test";
$topic = "test";

$mqtt =new phpMQTT($server,$port,$client_id);


$sql = "SELECT * FROM Buoy WHERE ID =?";

if($stmt = $conn ->prepare($sql)){
$stmt -> bind_param("i",$id);
	$result = $stmt -> execute();
	$store=	$stmt -> get_result();

	if($result){

		$buoy = array();
		$finalResult = array();

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

//			$smth = array("id"=>$id);
			array_push($buoy,$id);
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

			array_push($finalResult, $buoy);

			unset($buoy);
			$buoy= array();


		$json['buoy'] = $finalResult;
//		echo json_encode($json);
	
$resultString = "ID:".$id."\latitude:".$lat."\longitude:".$lng."\orientation:".$orientation."\LED1:".$LED1."\LED2:".$LED2."\LED3:".$LED3."\RGB1:".$RGB1."\RGB2:".$RGB2."\RGB3:".$RGB3."\target Lat:".$targetLat."\target Lng:".$targetLng."\hover:".$hover."\camera:".$camera."\n";

//echo $resultString;

	}else{

		echo "-1";
	}

}

if($mqtt -> connect(true,NULL,$username,$password)){


$mqtt ->publish($topic,"ID:".$id,0);
$mqtt ->publish($topic,"latitude:".$lat,0);
$mqtt ->publish($topic,"longitude:".$lng,0);
$mqtt ->publish($topic,"orientation:".$orientation,0);
$mqtt ->publish($topic,"LED1:".$LED1,0);
$mqtt ->publish($topic,"LED2:".$LED2,0);
$mqtt ->publish($topic,"LED3:".$LED3,0);
$mqtt ->publish($topic,"RGB1:".$RGB1,0);
$mqtt ->publish($topic,"RGB2:".$RGB2,0);
$mqtt ->publish($topic,"RGB3:".$RGB3,0);
$mqtt ->publish($topic,"targetLat:".$targetLat,0);
$mqtt ->publish($topic,"targetLng:".$TargetLng,0);
$mqtt ->publish($topic,"hover:".$hover,0);
$mqtt ->publish($topic,"camera:".$camera,0);

$mqtt -> close();

}else{

echo "time out";
}

?>
