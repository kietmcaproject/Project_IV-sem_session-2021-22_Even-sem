<?php

include("connect.php");
if(isset($_POST['name']))
{
	$e_id=$_POST['id'];
	$e_name=$_POST['name'];
	
	
	mysqli_query($con,"update events set e_name='$e_name' where e_id='$e_id' ");
	
	
	if(isset($_FILES['image']['name']))
	{
		$name=$_FILES['image']['name'];
		$tmpname=$_FILES['image']['tmp_name'];
		
		move_uploaded_file($tmpname,"images/".$name);
		$e_image="images/".$name;
		mysqli_query($con,"update events set e_image='$e_image' where e_id='$e_id' ");
	}
	
	$e_descri=$_POST['des'];
	mysqli_query($con,"update events set e_descri='$e_descri' where e_id='$e_id' ");
	$e_ref=$_POST['ref'];
	mysqli_query($con,"update events set e_ref='$e_ref' where e_id='$e_id' ");
	$e_uref=$_POST['uref'];
	mysqli_query($con,"update events set e_uref='$e_uref' where e_id='$e_id' ");

   	  if(mysqli_affected_rows($con)>0)
	  {
		 header("Location:eventabc.php?success");
	  }
	  else
	  {
		  header("Location:updateevent.php?error");
	  }

	
	
}


?>