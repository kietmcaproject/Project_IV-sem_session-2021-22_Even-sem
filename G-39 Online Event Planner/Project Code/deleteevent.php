<?php
include("connect.php");
  if(isset($_GET['e_id']))
  {
	  $id=$_GET['e_id'];
	  
	  mysqli_query($con,"delete from events where e_id = $id");
	  if(mysqli_affected_rows($con)>0)
	  {
		  header("Location:eventabc.php");
	  }
	  
  }


?>
