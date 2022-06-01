<?php include"connect.php";

$username=$_POST['user'];
$email=$_POST['email'];



 $res=mysqli_query($con,"select * from signupinfo where username='$username' and Email='$email' ");
	  
	 if(mysqli_affected_rows($con)==1)
	{
		header("Location:headeruser.php");
	}
	else
	{
		header("Location:emaillogin.php?err=Username and e-mail id is Incorrect..");
		
	}
	  
?>