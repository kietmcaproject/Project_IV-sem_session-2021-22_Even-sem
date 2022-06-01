<?php include"connect.php";
 session_start();
 if(isset($_GET['d_id']))
  {        $id=$_GET['d_id'];	 
  			$res= mysqli_query($con,"select * from decoration where d_id=$id")or die(mysqli_error($con));
			if($row=mysqli_fetch_array($res))
	  		{
				$d_name=$row[1];
		  		$d_price=$row[6];
	  		}
			$username=$_SESSION['my'];
			$resu=mysqli_query($con,"insert into addtocart values('$username','$d_name','$d_price')")or die(mysqli_error($con));
}
			if(mysqli_affected_rows($con)==1)
			{
				header("Location:userdecoration.php?success");
			}
			else
			{
				header("Location:userdecoration.php?err=Username..");
			}
?>