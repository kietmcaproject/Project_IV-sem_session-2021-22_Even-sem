<?php include"connect.php";

$e_name=$_POST['name'];

$tmpname=$_FILES['image']['tmp_name'];
$filename=$_FILES['image']['name'];
$filetype=$_FILES['image']['type'];
$filesize=$_FILES['image']['size'];

move_uploaded_file($tmpname,"images/".$filename);
  
$e_image="images/".$filename;
  
$e_descri=$_POST['des'];
$e_ref=$_POST['ref'];
$e_uref=$_POST['uref'];
  mysqli_query($con,"insert into events(e_name,e_image,e_descri,e_ref,e_uref) values('$e_name','$e_image','$e_descri','$e_ref','$e_uref')")or die(mysqli_error($con));
	  
	  if(mysqli_affected_rows($con)>0)
	  {
		 header("Location:eventabc.php?success");
	  }
	  else
	  {
		  header("Location:addevent.php?error");
	  }
	  
?>