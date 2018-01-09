<?php
/*
*Created by Bougioklis George
*/
// Creation file for our database
$serverName = "localhost";
$username = "bougioklis";
$password = "raspberry";

// connection to mysql
$connection = mysqli_connect($serverName, $username,$password );

if(!$connection){
	die("Connection Failed : ".mysqli_connect_error());
}

// creation of the database
$sql = "CREATE DATABASE IF NOT EXISTS smart_buoy";

$result = mysqli_query($connection,$sql);

if($result){
//if database connection was successful create table
	$sql1="USE smart_buoy";
	$result1 = mysqli_query($connection,$sql1);
	if($result1){
		$sql2= "CREATE TABLE IF NOT EXISTS Buoy ( ID int(11) AUTO_INCREMENT PRIMARY KEY, latitude DECIMAL(10,8) ,longitude DECIMAL(10,8), orientation int(11) , LED1 ENUM('0', '1'), LED2 ENUM('0', '1'), LED3 ENUM('0', '1'), RGB1 varchar(22), RGB2 varchar(22), RGB3 varchar(22),targetLat DECIMAL(10,8),targetLng DECIMAL(10,8),hoverFlag ENUM('0', '1'), cameraFlag ENUM('0', '1'))";
		$result2 = mysqli_query($connection,$sql2);
		if($result2){
			//Delete this statement insertion on deploy
			$sql3="INSERT INTO Buoy (latitude,longitude,orientation,LED1,LED2,LED3,RGB1,RGB2,RGB3,targetLat,targetLng,hoverFlag,cameraFlag) VALUES (40.604446,22.919463,255,'0','1','1','#ccff66','#111a00','off',40.60457,22.353165,'0','1'),(40.596307,22.931974,215,'1','0','1','#1aff1a','#00e600','#ffffff',40.603807,22.933165,'1','0'), (40.602507,22.944018,64,'0','0','0','off','off','off',40.602532,22.944147,'0','0')";
			$result3=mysqli_query($connection,$sql3);
			if($result3){
				echo "Database Created Successfully";
			}
		}
	}
}
?>
