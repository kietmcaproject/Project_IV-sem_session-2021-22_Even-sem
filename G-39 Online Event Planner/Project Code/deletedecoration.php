<?php
include("connect.php");
  if(isset($_GET['d_id']))
  {
	  $id=$_GET['d_id'];
	  
	  mysqli_query($con,"delete from decoration where d_id = $id");
	  if(mysqli_affected_rows($con)>0)
	  {
		  header("Location:decorationabc.php");
	  }
	  
  }


?>
