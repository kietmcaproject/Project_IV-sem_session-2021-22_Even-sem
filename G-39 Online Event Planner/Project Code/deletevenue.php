<?php
include("connect.php");
  if(isset($_GET['v_id']))
  {
	  $id=$_GET['v_id'];
	  
	  mysqli_query($con,"delete from venue where v_id = $id");
	  if(mysqli_affected_rows($con)>0)
	  {
		  header("Location:venueabc.php");
	  }
	  
  }


?>
