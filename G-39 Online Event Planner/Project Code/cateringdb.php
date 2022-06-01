<?php include"connect.php";

$c_name=$_POST['name'];

$tmpname=$_FILES['image']['tmp_name'];
$filename=$_FILES['image']['name'];
$filetype=$_FILES['image']['type'];
$filesize=$_FILES['image']['size'];

move_uploaded_file($tmpname,"images/".$filename);
  
$c_image="images/".$filename;
  
$c_descri=$_POST['des'];
$c_ref=$_POST['ref'];
$c_uref=$_POST['uref'];
$c_ingredient=$_POST['ind'];
$c_price=$_POST['pri'];
  mysqli_query($con,"insert into catering(c_name,c_image,c_descri,c_ref,c_uref,c_ingredient,c_price) values('$c_name','$c_image','$c_descri','$c_ref','$c_uref','$c_ingredient','$c_price')")or die(mysqli_error($con));
	  
	  if(mysqli_affected_rows($con)>0)
	  {
		 header("Location:cateringabc.php?success");
	  }
	  else
	  {
		  header("Location:addcatering.php?error");
	  }
	  
?>