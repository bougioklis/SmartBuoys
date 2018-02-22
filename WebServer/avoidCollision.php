<?php

/* 
* Created By Bougioklis George
*
* alert about collision between Buoys
*
* 
*/

require 'Init.php';

//select all Buoys
$sql = "SELECT * FROM Coor";
$result = $conn -> query($sql);

if($result){

	//create 2d array with DB's results
	$coor = array();

	//fetch results
	while($row = $result -> fetch_assoc() ){

		$id = $row["ID"];
		$latFrom = $row["latitudeFrom"];
		$lngFrom = $row["longitudeFrom"];
		$latTo = $row["latitudeTo"];
		$lngTo = $row["longitudeTo"];

		$coor[] = array(floatval($latFrom),floatval($lngFrom),floatval($latTo),floatval($lngTo));

	}
	$collinearBuoys="";
	// check for every buoy if the distance between itself and all others buoys is less than 5 m
	// and if its previous coordinates, its current coordinates are collinear with the current coordinates from another buoy 
	for($i=0;$i<count($coor);$i++){
		for($j=0;$j<count($coor);$j++){

			if($i==$j)
				continue;
			else{
				if(/*(calculateDistance($coor[$i][2],$coor[$i][3],$coor[$j][2],$coor[$j][3]) < 5) &&*/ colinearity($coor[$i][0],$coor[$i][1],$coor[$i][2],$coor[$i][3],$coor[$j][2],$coor[$j][3])){
//					echo "WE HAVE A WINNER".$i." ".$j."<br>";
					$first = $i+1;
					$second = $j+1;
					$collinearBuoys .= "{$first},{$second}###";
				}else{
//					echo "EVERYONE ARE LOSERS" .$i." ".$j."<br>";
				}
			}
		}
	}

if(empty($collinearBuoys)){
echo "-1";
}else{
echo $collinearBuoys;
}

}

function calculateDistance($latFrom,$lngFrom,$latTo,$lngTo){

	$earthRadius = 6371000;

	$latFrom = deg2rad($latFrom);
	$lngFrom = deg2rad($lngFrom);
	$latTo   = deg2rad($latTo);
	$lngTo   = deg2rad($lngTo);
	
	$latDelta = $latTo - $latFrom;
	$lngDelta = $lngTo - $lngFrom;

	$angle = 2 * asin(sqrt(pow(sin($latDelta/2),2) + cos($latFrom) * cos($latTo) * pow(sin($lngDelta / 2),2 )));
	return $angle * $earthRadius; 
}


/*
*	(y2-y1)			(y3-y2)
*	-------		=	-------
*	(x2-x1)			(x3-x2)
*
*	if those two fractions are equals then the three points are collinear
*/
function colinearity ($latFrom,$lngFrom,$latTo,$lngTo,$secondLat,$secondLng){

	$Fraction1 = ($lngTo - $lngFrom) / ($latTo - $latFrom);
	$Fraction2 = ($secondLng - $lngTo) / ($secondLat - $latTo);

	if($Fraction1 == $Fraction2){
		return true;
	}else{
	 	return false; 
	}


}

?>
