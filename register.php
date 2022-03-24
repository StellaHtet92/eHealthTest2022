<?php
require "conn.php";
$username=$_POST["username"];
$password=$_POST["password"];
$mobile=$_POST["mobile"];
$dateString=$_POST["dateOfBirth"];
$dateOfBirth = \DateTime::createFromFormat('Y-m-d', $dateString);
$dateOfBirth=$dateOfBirth ->format('Y-m-d');
//echo($dateOfBirth);
$gender=$_POST["gender"];
$bloodType=$_POST["bloodType"];

/*$username="Abdo";
$password="1234567";
$mobile="09123456";
$dateString = '28/07/2016';
$dateOfBirth = \DateTime::createFromFormat('d/m/Y', $dateString);
$dateOfBirth=$dateOfBirth ->format('Y-m-d');
echo($dateOfBirth);
$gender="Male";
$bloodType="A";*/
$response=array();
if($conn)
{
	if(strlen($password)>40 || strlen($password)<6)
	{
		echo "Password must be less than 40 and greater than 6 characters".$password;
	}
	else
	{
		$sqlCheckUsername="SELECT * FROM users_table where 'username' LIKE '$username'";
		$usernameQuery=mysqli_query($conn,$sqlCheckUsername);
		$sqlCheckMobile="SELECT * FROM users_table where 'mobile' LIKE '$mobile'";
		$mobileQuery=mysqli_query($conn,$sqlCheckMobile);
		
		if(mysqli_num_rows($usernameQuery)>0)
		{
			echo "Username already existed, kindly change username";
		}
		else if(mysqli_num_rows($mobileQuery)>0)
		{
			echo "This mobile number is already registered, kindly change mobile number.";
		}
		else
		{
			$sql_register="INSERT INTO users_table (`username`,`password`,`mobile`,`dateOfBirth`,`gender`,`bloodType`) VALUES ('$username','$password','$mobile','$dateOfBirth','$gender','$bloodType')";
			//$result = mysqli_query($conn, $sql_register) or trigger_error("Query Failed! SQL: $sql_register - Error: ".mysqli_error($conn), E_USER_ERROR);
			if(mysqli_query($conn,$sql_register))
			{
				$user_id=mysqli_insert_id($conn);
				//echo"user_id".$user_id;
				//echo "Successfully Registered.";
				$response['error'] = false;
                $response['message'] = "User registered successfully!";
				$response['user_id']=$user_id;
			}
			else
			{
				//echo "Failed to register";
				 $response['error'] = true;
                 $response['message'] = "failed\n ".$conn->error;
				 $response['user_id']=null;
			}
		}
		
	}
}
else
{
	//echo "Connection Error";
	 $response['error'] = true;
     $response['message'] = "Connection Error: failed\n ".$conn->error;
	 $response['user_id']=null;
}
echo json_encode($response);
mysqli_close($conn);
?>