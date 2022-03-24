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
$username = $_POST['username'];

//creating a statement to insert to database
$sql = "SELECT `BMI`,`weight`,`height` FROM user_basic_info where `user_id` LIKE '$user_id' AND `name` LIKE '$username' LIMIT 1";
$result = $conn->query($sql);

//if data inserts successfully
if(mysqli_query($conn,$sql)){
//making success response
if ($result->num_rows > 0) {
  // output data of each row
  while($row = $result->fetch_assoc()) {
    $response['error'] = false;
    $response['BMI']=$row["BMI"];
	$response['weight']=$row["weight"];
	$response['height']=$row["height"];
    $response['message'] = 'Data selected successfully';
    $conn->close();
  }
} else {
  $response['error'] = false;
    $response['BMI']=0;
	$response['weight']=0;
	$response['height']=0;
    $response['message'] = 'No Data found';
    $conn->close();
}

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