<?php include"connect.php";

$d_name=$_POST['name'];

$tmpname=$_FILES['image']['tmp_name'];
$filename=$_FILES['image']['name'];
$filetype=$_FILES['image']['type'];
$filesize=$_FILES['image']['size'];

move_uploaded_file($tmpname,"images/".$filename);
  
$d_image="images/".$filename;
  
$d_descri=$_POST['des'];
$d_ref=$_POST['ref'];
$d_uref=$_POST['uref'];
$d_price=$_POST['pri'];
  mysqli_query($con,"insert into decoration(d_name,d_image,d_descri,d_ref,d_uref,d_price) values('$d_name','$d_image','$d_descri','$d_ref','$d_uref','$d_price')")or die(mysqli_error($con));
	  
	  if(mysqli_affected_rows($con)>0)
	  {
		 header("Location:decorationabc.php?success");
	  }
	  else
	  {
		  header("Location:adddecoration.php?error");
	  }
	  
?>