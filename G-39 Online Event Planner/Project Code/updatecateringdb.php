<?php

include("connect.php");
if(isset($_POST['name']))
{
	$c_id=$_POST['id'];
	$c_name=$_POST['name'];
	
	
	mysqli_query($con,"update catering set c_name='$c_name' where c_id='$c_id' ");
	
	
	if(isset($_FILES['image']['name']))
	{
		$name=$_FILES['image']['name'];
		$tmpname=$_FILES['image']['tmp_name'];
		
		move_uploaded_file($tmpname,"images/".$name);
		$c_image="images/".$name;
		mysqli_query($con,"update catering set c_image='$c_image' where c_id='$c_id' ");
	}
	
	$c_descri=$_POST['des'];
	mysqli_query($con,"update catering set c_descri='$c_descri' where c_id='$c_id' ");
	$c_ref=$_POST['ref'];
	mysqli_query($con,"update catering set c_ref='$c_ref' where c_id='$c_id' ");
	$c_uref=$_POST['uref'];
	mysqli_query($con,"update catering set c_uref='$c_uref' where c_id='$c_id' ");
	$c_ingredient=$_POST['ind'];
	mysqli_query($con,"update catering set c_ingredient='$c_ingredient' where c_id='$c_id' ");
	$c_price=$_POST['pri'];
	mysqli_query($con,"update catering set c_price='$c_price' where c_id='$c_id' ");

   	  if(mysqli_affected_rows($con)>0)
	  {
		 header("Location:cateringabc.php?success");
	  }
	  else
	  {
		  header("Location:updatecatering.php?error");
	  }
}


?>