<?php include"connect.php";

$f_username=$_POST['user'];
$f_email=$_POST['email'];
$f_subject=$_POST['sub'];
$f_feedback=$_POST['feed'];

 $res=mysqli_query($con,"insert into feedback values('$f_username','$f_email','$f_subject','$f_feedback')")or die(mysqli_error($con));
	  
	 if(mysqli_affected_rows($con)>0)
	{
		header("Location:homeuser.php?success");
	}
	else
	{
		header("Location:feedback.php?error");
		
	}
	  
?>