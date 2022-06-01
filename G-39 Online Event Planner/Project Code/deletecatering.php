<?php
include("connect.php");
  if(isset($_GET['c_id']))
  {
	  $id=$_GET['c_id'];
	  
	  mysqli_query($con,"delete from catering where c_id = $id");
	  if(mysqli_affected_rows($con)>0)
	  {
		  header("Location:cateringabc.php");
	  }
	  
  }


?>
