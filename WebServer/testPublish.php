<?php 

function MQTTPublish($topic,$id){

require ("phpMQTT.php");
require 'Init.php';

//$id = $_POST['id'];


$server= "192.168.0.25";
$port =1883;
$username = "";
$password = "";
$client_id= "Buoy";

$mqtt =new phpMQTT($server,$port,$client_id);


$sql = "SELECT * FROM Buoy WHERE ID =?";

if($stmt = $conn ->prepare($sql)){
$stmt -> bind_param("i",$id);
	$result = $stmt -> execute();
	$store=	$stmt -> get_result();

	if($result){

		$buoy = array();
		$finalResult = array();
//pernoume tis plhrofories tis shmadouras
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
$mqtt ->publish($topic,"targetLng:".$targetLng,0);
$mqtt ->publish($topic,"hover:".$hover,0);
$mqtt ->publish($topic,"camera:".$camera,0);

$mqtt -> close();

}else{

echo "time out";
}

}
?>
