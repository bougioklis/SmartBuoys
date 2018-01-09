<?php
/*
* Created By Bougioklis George
*/

//Connection to Database
$servername = "localhost";
$username = "bougioklis";
$password = "raspberry";
$db = "smart_buoy";

$conn = new mysqli($servername,$username,$password,$db);

if($conn -> connect_error){
	die("Connection failed: " . $conn -> connect_error);
}

?>

