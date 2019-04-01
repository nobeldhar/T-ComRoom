<?php

require "init.php";

$email=$_GET['email'];
$password=$_GET['password'];
$name=$_GET['name'];
$institution=$_GET['institution'];
$department=$_GET['department'];
$reg=$_GET['reg'];
$year_semester=$_GET['year_semester'];
$phone=$_GET['phone'];


$response=array();


$sql="select * from students where password like '$password' and email like '$email';";

$sql_reg="insert into students (email,password,name,reg,institution,department,year_semester,phone)
values ('$email','$password','$name','$reg','$institution','$department','$year_semester','$phone');";



$result=mysqli_query($connection, $sql);

if(mysqli_num_rows($result)>0){
  $status="exist";
}else{
  if(mysqli_query($connection,$sql_reg)){

  	$new=mysqli_query($connection, $sql);
  	while($row = mysqli_fetch_array($new)){

  	$response['user_id']=$row['student_id'];
  	$response['user']="Student";
  	$response['institution']=$institution;
    $status="ok";
    $response['name']=$name;
    $response['email']=$email;
}

  }else {
      $status = "error";
  }
}



$response["response"]=$status;;
$output= json_encode($response);
echo $output;
mysqli_close($connection);

?>
