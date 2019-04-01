<?php

require "init.php";


$teacher_id=$_GET['teacher_id'];
$student_id=$_GET['student_id'];

$response=array();

$sql="delete from appointments where teacher_id = '$teacher_id' and student_id= '$student_id';";

  if(mysqli_query($connection,$sql)){
      $status="ok";
  }else {
      $status = "error";
  }

$response["response"]=$status;;
$output= json_encode($response);
echo $output;
mysqli_close($connection);

?>