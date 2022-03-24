<?php
require "conn.php";
//getting passed information
$userID=(int)$_POST["user_id"];
$username=$_POST["username"];
$name=$_POST["name"];
$HCV=$_POST["HCV"];
$TB=$_POST["TB"];
$HCB=$_POST["HCB"];
$HIV=$_POST["HIV"];
$Diabetes=$_POST["Diabetes"];
$Other=$_POST["Other"];
$Allergies=$_POST["Allergies"];
$height=(int)$_POST["height"];
$weight=(int)$_POST["weight"];
$BMI=(double)$_POST["BMI"];
$Smoking_status=$_POST["Smoking_status"];
$Num_Ciga_per_day=(int)$_POST["Num_Ciga_per_day"];
$created_at_dateString=$_POST["created_at"];
$created_at = \DateTime::createFromFormat('Y-m-d H:i:s', $created_at_dateString); /*('Y-m-d H:i:s', $created_at_dateString);*/
$created_at=$created_at ->format('Y-m-d H:i:s');
echo($created_at);
$updated_at_dateString=$_POST["updated_at"];
$updated_at = \DateTime::createFromFormat('Y-m-d H:i:s', $updated_at_dateString);
$updated_at=$updated_at ->format('Y-m-d H:i:s');
echo($updated_at);
//To test
/*
$userID=1;
$username="Abdo";
$name="Test User";
$HCV="Yes";
$TB="Yes";
$HCB="Yes";
$HIV="Yes";
$Diabetes="Yes";
$Other="No";
$Allergies="Test Allergies";
$height=160;
$weight=59;
$BMI=23.5;
$Smoking_status="Yes";
$Num_Ciga_per_day=1;
$created_at_dateString='2021-08-18 12:28:00';
$created_at = \DateTime::createFromFormat('Y-m-d H:i:s', $created_at_dateString);
$created_at=$created_at ->format('Y-m-d H:i:s');
echo($created_at);
$updated_at_dateString='2021-08-18 12:28:00';
$updated_at = \DateTime::createFromFormat('Y-m-d H:i:s', $updated_at_dateString);
$updated_at=$updated_at ->format('Y-m-d H:i:s');
echo($updated_at);
*/
$response=array();
if($conn)
{
	if(isset($userID) && isset($username) && isset($name) && isset($HCV)&& isset($TB)&& isset($HCB)&& isset($Diabetes)&& isset($Other)&& isset($Allergies)
		&& isset($height)&& isset($weight)&& isset($BMI)&& isset($Smoking_status)&& isset($Num_Ciga_per_day)&& isset($created_at)&& isset($updated_at))
	{
		$sqlCheckUsername="SELECT * FROM users_table WHERE ID=$userID and username LIKE '$username'";
		$usernameQuery=mysqli_query($conn,$sqlCheckUsername);
		
		if(mysqli_num_rows($usernameQuery)>0)
		{
			$sql_insert_basic_info="INSERT INTO user_basic_info (`user_id`,`name`,`HCV`,`HCB`,`TB`,`HIV`,`Diabetes`,`Other`,`Allergies`,`weight`,`height`,`BMI`,`Smoking_Status`,`Num_Ciga_per_day`,`created_at`,`updated_at`)
			VALUES ('$userID','$username','$name','$HCV','$TB','$HCB','$Diabetes','$Other','$Allergies','$height','$weight','$BMI','$Smoking_status','$Num_Ciga_per_day','$created_at','$updated_at')";
			//$result = mysqli_query($conn, $sql_register) or trigger_error("Query Failed! SQL: $sql_register - Error: ".mysqli_error($conn), E_USER_ERROR);
			if(mysqli_query($conn,$sql_insert_basic_info))
			{
				//$user_id=mysqli_insert_id($conn);
				//echo "Successfully Saved.";
				$response['error'] = false;
                $response['message'] = "User Information saved successfully!";
				//$response['user_id']=$user_id;
			}
			else
			{
				//echo "Failed to register";
				 $response['error'] = true;
                 $response['message'] = "failed\n ".$conn->error;
				// $response['user_id']=null;
			}
		}
		else
		{
			//echo "User does not exists!!";
			$response['error'] = true;
            $response['message'] = "User does not exists!!";
		}
	}
	else
	{
		
		//echo "Please enter complete information.";
		$response['error'] = true;
        $response['message'] = "Please enter complete information.";
	}
}
else
{
	//echo "Connection Error";
	 $response['error'] = true;
     $response['message'] = "failed\n ".$conn->error;
	// $response['user_id']=null;
}
echo json_encode($response);
mysqli_close($conn);
?>