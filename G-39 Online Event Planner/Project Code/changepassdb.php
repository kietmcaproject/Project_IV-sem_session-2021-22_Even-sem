<?php include"connect.php";
if(isset($_POST['opass']))
{
	$username=$_POST['user'];
	$password=$_POST['pass'];
	mysqli_query($con,"update signupinfo set password='$password', cpassword='$password' where username='$username' ");
	header("Location:login.php");
}
	else
	{
		header("Location:changepass.php?err=Password not changed..");
	}
?>