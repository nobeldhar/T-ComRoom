<?php


require "init.php";

$institution="%".$_GET['institution']."%";
	
$sql_query="SELECT * FROM teachers WHERE institution LIKE '$institution';";
		
			$response2=array();
			$result=mysqli_query($connection, $sql_query);
			while($row = mysqli_fetch_array($result)){
				
			$response=array();
			$response['teacher_id']=$row['teacher_id'];
			$response['name']=$row['name'];
  			$response['institution']=$row['institution'];
  			$response['department']=$row['department'];
  			$response['phone']=$row['phone'];  			
  			array_push($response2, $response);	
			}
			
			$output= json_encode($response2);
			echo $output;


mysqli_close($connection);

?>



