<?php include"connect.php";

$a_name=$_POST['name'];;
$a_password=$_POST['pass'];
$a_email=$_POST['email'];
$a_contact=$_POST['mob'];



  mysqli_query($con,"insert into admin(a_name,a_password,a_email,a_contact)  values('$a_name','$a_password','$a_email','$a_contact')")or die(mysqli_error($con));
  
  
	  
	  if(mysqli_affected_rows($con)>0)
	  {
		 header("Location:adminlogin.php?success");
	  }
	  else
	  {
		  header("Location:adminsignup.php?error");
	  }
	  
?>