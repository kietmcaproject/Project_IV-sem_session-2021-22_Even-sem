<?php include"connect.php";

$b_cutomername=$_POST['customer'];
$b_email=$_POST['email'];
$b_mobno=$_POST['mob'];
$b_numguest=$_POST['nog'];
$b_edate=$_POST['edate'];
$b_event=$_POST['event'];
 $res=mysqli_query($con,"insert into booking(b_customername,b_email,b_mobno,b_numguest,b_edate,b_event) values('$b_cutomername','$b_email','$b_mobno','$b_numguest','$b_edate','$b_event')")or die(mysqli_error($con));
	  
	 if(mysqli_affected_rows($con)>0)
	{
		header("Location:book2.php?id='$b_email'");
	}
	else
	{
		header("Location:booking.php?err=booking failed..");
		
	}
	  
?>