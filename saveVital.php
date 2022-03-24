<?php
/*
* Database Constants
* Make sure you are putting the values according to your database here
*/
define('DB_HOST','localhost');
define('DB_USERNAME','root');
define('DB_PASSWORD','');
define('DB_NAME', 'his');
//Connecting to the database
$conn = new mysqli(DB_HOST, DB_USERNAME, DB_PASSWORD, DB_NAME);
//checking the successful connection
if($conn->connect_error) {
die("Connection failed: " . $conn->connect_error);
}
//making an array to store the response
$response = array();
//if there is a post request move ahead
if($_SERVER['REQUEST_METHOD']=='POST'){
//getting the ecg data from request

$user_id = $_POST['user_id'];
//$vital_id = $_POST['vital_id'];
$EWS = $_POST['EWS'];
$temp = $_POST['temp'];
$BP_Sys = $_POST['BP_Sys'];
$BP_Dia = $_POST['BP_Dia'];
$spO2 = $_POST['spO2'];
$pulse = $_POST['pulse'];
$blood_sugar_level = $_POST['blood_sugar_level'];
$mealStatus=$_POST['mealStatus'];
$HDL = $_POST['HDL'];
$LDL = $_POST['LDL'];
$heart_rate = $_POST['heart_rate'];
$level_of_consciousness = $_POST['level_of_consciousness'];
$last_menstruation_dateString = $_POST['last_menstruation_date'];
$last_menstruation_date= \DateTime::createFromFormat('Y-m-d', $last_menstruation_dateString);
$last_menstruation_date=$last_menstruation_date ->format('Y-m-d');
$dateString=$_POST['collected_datetime'];
$createdAt = \DateTime::createFromFormat('Y-m-d H:i:s', $dateString);
$createdAt=$createdAt ->format('Y-m-d H:i:s');

//creating a statement to insert to database
$sql = "INSERT INTO vital_records (`user_id`, `EWS`, `temp`, `BP_Sys`, `BP_Dia`, `spO2`, `pulse`, `blood_sugar_level`, `mealStatus`, `HDL`, `LDL`, `heart_rate`, `level_of_consciousness`, `last_menstruation_date`, `collected_datetime`) VALUES
 ('$user_id','$EWS','$temp','$BP_Sys','$BP_Dia','$spO2','$pulse','$blood_sugar_level','$mealStatus','$HDL','$LDL', '$heart_rate','$level_of_consciousness','$last_menstruation_date','$createdAt')";

//if data inserts successfully
if(mysqli_query($conn,$sql)){
//making success response
$response['error'] = false;
$response['message'] = 'Vital Data saved successfully';
}
else{
//if not making failure response
$response['error'] = true;
$response['message'] = "Error: ".$sql." => ".$conn->error;;
}
$conn->close();
}
else{
$response['error'] = true;
$response['message'] = "There is no post request";
}
//displaying the data in json format
echo json_encode($response);