<?php

include("connect.php");
if(isset($_POST['name']))
{
	$v_id=$_POST['id'];
	$v_name=$_POST['name'];
	
	
	mysqli_query($con,"update venue set v_name='$v_name' where v_id='$v_id' ");
	
	
	if(isset($_FILES['image']['name']))
	{
		$name=$_FILES['image']['name'];
		$tmpname=$_FILES['image']['tmp_name'];
		
		move_uploaded_file($tmpname,"images/".$name);
		$v_image="images/".$name;
		mysqli_query($con,"update venue set v_image='$v_image' where v_id='$v_id' ");
	}
	
	$v_address=$_POST['add'];
	mysqli_query($con,"update venue set v_address='$v_address' where v_id='$v_id' ");
	$v_descri=$_POST['des'];
	mysqli_query($con,"update venue set v_descri='$v_descri' where v_id='$v_id' ");
	$v_ref=$_POST['ref'];
	mysqli_query($con,"update venue set v_ref='$v_ref' where v_id='$v_id' ");
	$v_uref=$_POST['uref'];
	mysqli_query($con,"update venue set v_uref='$v_uref' where v_id='$v_id' ");
	$v_price=$_POST['pri'];
	mysqli_query($con,"update venue set v_price='$v_price' where v_id='$v_id' ");

   	  if(mysqli_affected_rows($con)>0)
	  {
		 header("Location:venueabc.php?success");
	  }
	  else
	  {
		  header("Location:updatevenue.php?error");
	  }
}


?>