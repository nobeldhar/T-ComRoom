<?php


require "init.php";

$user_id=$_GET['user_id'];
	
$sql_query="select * from students inner join requests on students.student_id=requests.student_id where requests.teacher_id='$user_id';";
		
			$response2=array();
			$result=mysqli_query($connection, $sql_query);
			while($row = mysqli_fetch_array($result)){
				
			$response=array();
			$response['student_id']=$row['student_id'];
			$response['name']=$row['name'];
			$response['reg']=$row['reg'];
  			$response['institution']=$row['institution'];
  			$response['department']=$row['department'];
  			$response['year_semester']=$row['year_semester'];
  			$response['phone']=$row['phone'];
  			$response['subject']=$row['subject'];
  			$response['description']=$row['description'];  			
  			array_push($response2, $response);	
			}
			
			$output= json_encode($response2);
			echo $output;


mysqli_close($connection);

?>



