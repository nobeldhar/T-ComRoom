<?php

require "init.php";


$teacher_id=$_GET['teacher_id'];
$student_id=$_GET['student_id'];
$message=$_GET['message'];
$date=$_GET['date'];
$time=$_GET['time'];
$subject=$_GET['subject'];
$description=$_GET['description'];


$response=array();

$sql_reg="insert into appointments values ('$teacher_id','$student_id','$subject','$description','$message','$date','$time');";
$sql="delete from requests where teacher_id = '$teacher_id' and student_id= '$student_id';";

  if(mysqli_query($connection,$sql_reg)){
    if(mysqli_query($connection,$sql)){
        $status="ok";
    }else{
        $status = "erro"; 
    }
  }else {
      $status = "error";
  }




$response["response"]=$status;;
$output= json_encode($response);
echo $output;
mysqli_close($connection);

?>