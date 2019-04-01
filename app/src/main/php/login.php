<?php


require "init.php";


$email=$_GET['email'];
$password=$_GET['password'];

$sql_query="select * from students where email like '$email' and password like '$password';";

$sql_query2="select * from teachers where email like '$email' and password like '$password';";

	$result=mysqli_query($connection, $sql_query);
	$result2=mysqli_query($connection, $sql_query2);
	if(mysqli_num_rows($result)>0 || mysqli_num_rows($result2)>0 ){
		
		if(mysqli_num_rows($result)>0){
			$row=mysqli_fetch_assoc($result);
			$status="ok";
			$response['user_id']=$row['student_id'];
  			$response['user']="Student";
  			$response['institution']=$row['institution'];
    		$response['name']=$row['name'];
    		$response['email']=$row['email'];
    		$response["response"]=$status;;
			$output= json_encode($response);
			echo $output;
		}else{
			$row=mysqli_fetch_assoc($result2);
			$status="ok";
			$response['user_id']=$row['teacher_id'];
  			$response['user']="Teacher";
  			$response['institution']=$row['institution'];
    		$response['name']=$row['name'];
    		$response['email']=$row['email'];
    		$response["response"]=$status;;
			$output= json_encode($response);
			echo $output;
		}

	}else {
		$status="failed";
		echo json_encode(array("response" => $status));
		
	}

mysqli_close($connection);

?>