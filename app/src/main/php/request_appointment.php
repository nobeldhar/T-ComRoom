<?php

require "init.php";


$teacher_id=$_GET['teacher_id'];
$student_id=$_GET['student_id'];
$subject=$_GET['subject'];
$description=$_GET['description'];


$response=array();

$sql_reg="insert into requests values ('$teacher_id','$student_id','$subject','$description');";

  if(mysqli_query($connection,$sql_reg)){

    $status="ok";
  }else {
      $status = "error";
  }




$response["response"]=$status;;
$output= json_encode($response);
echo $output;
mysqli_close($connection);

?>