<?php

include("connect.php");
if(isset($_POST['name']))
{
	$d_id=$_POST['id'];
	$d_name=$_POST['name'];
	
	
	mysqli_query($con,"update decoration set d_name='$d_name' where d_id='$d_id' ");
	
	
	if(isset($_FILES['image']['name']))
	{
		$name=$_FILES['image']['name'];
		$tmpname=$_FILES['image']['tmp_name'];
		
		move_uploaded_file($tmpname,"images/".$name);
		$d_image="images/".$name;
		mysqli_query($con,"update decoration set d_image='$d_image' where d_id='$d_id' ");
	}
	
	$d_descri=$_POST['des'];
	mysqli_query($con,"update decoration set d_descri='$d_descri' where d_id='$d_id' ");
	$d_ref=$_POST['ref'];
	mysqli_query($con,"update decoration set d_ref='$d_ref' where d_id='$d_id' ");
	$d_uref=$_POST['uref'];
	mysqli_query($con,"update decoration set d_uref='$d_uref' where d_id='$d_id' ");
	$d_price=$_POST['pri'];
	mysqli_query($con,"update decoration set d_price='$d_price' where d_id='$d_id' ");

   	  if(mysqli_affected_rows($con)>0)
	  {
		 header("Location:decorationabc.php?success");
	  }
	  else
	  {
		  header("Location:updatedecoration.php?error");
	  }
}


?>