<?php include"connect.php";
$a_name=$_POST['name'];
$a_password=$_POST['pass'];
 $res=mysqli_query($con,"select * from admin where a_name='$a_name' and a_password='$a_password'");   
	 if(mysqli_affected_rows($con)==1)
	{
		session_start();
		$_SESSION['my']=$a_name;
		header("Location:admin.php");
	}
	else
	{
		header("Location:adminlogin.php?err=Username and password is Incorrect..");
	}
?>