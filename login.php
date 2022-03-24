<?php
require "conn.php";
$username=$_POST["username"];
$password=$_POST["password"];

/*$username="yuyu";
$password="1234567";*/
$response=array();
if($conn)
{
    $sqlCheckUser="SELECT * FROM users_table where username LIKE '$username' AND password LIKE '$password'";
   // echo "".$sqlCheckUser;
	$result=mysqli_query($conn,$sqlCheckUser);
    if(mysqli_num_rows($result)>0)
		{
            while($row = mysqli_fetch_assoc($result))
            {
                $user_id =  $row['ID'];
                $response['error'] = false;
                $response['message'] = "Log In Successfully";
                $response['user_id']=$user_id;
            }
            
           
		}
        else
        {
            //echo "Failed to register";
             $response['error'] = true;
             $response['message'] = "failed\n ".mysqli_num_rows($result);
             $response['user_id']=null;
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