<?php include"connect.php";

$username=$_POST['user'];
$password=$_POST['pass'];


 $res=mysqli_query($con,"select * from signupinfo where username='$username' and password='$password'");
 
      
    
	 if(mysqli_affected_rows($con)==1)
	{
		session_start();
		
		
		
		$_SESSION['my']=$username;
		header("Location:booking.php");
	}
	else
	{
		header("Location:login.php?err=Username and password is Incorrect..");
		
	}
	  
?>