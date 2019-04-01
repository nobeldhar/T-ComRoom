<?php


require "init.php";

$user_id=$_GET['user_id'];
	
$sql_query="SELECT * FROM appointments INNER join teachers on teachers.teacher_id=appointments.teacher_id WHERE appointments.student_id='$user_id';";
		
			$response2=array();
			$result=mysqli_query($connection, $sql_query);
			while($row = mysqli_fetch_array($result)){
				
			$response=array();
			$response['teacher_id']=$row['teacher_id'];
			$response['teacher_name']=$row['name'];
  			$response['institution']=$row['institution'];
  			$response['department']=$row['department'];
  			$response['phone']=$row['phone'];
  			$response['subject']=$row['subject'];
  			$response['description']=$row['description'];
  			$response['message']=$row['message'];
  			$response['date']=$row['date'];
  			$response['time']=$row['time'];  			
  			array_push($response2, $response);	
			}
			
			$output= json_encode($response2);
			echo $output;


mysqli_close($connection);

?>